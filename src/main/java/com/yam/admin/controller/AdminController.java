package com.yam.admin.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yam.admin.dto.AdminDTO;
import com.yam.admin.model.Admin;
import com.yam.admin.service.AdminService;
import com.yam.customer.member.domain.BlacklistedMember;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.BlacklistedMemberRepository;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.store.BlacklistedStore;
import com.yam.store.Store;
import com.yam.store.repository.BlacklistedStoreRepository;
import com.yam.store.repository.StoreRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private StoreRepository storeRepository; // ✅ 사업자 서비스 추가

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private BlacklistedMemberRepository blacklistedMemberRepository;

	@Autowired
	private BlacklistedStoreRepository blacklistedStoreRepository;

	// 애플리케이션 실행 시 기본 경로로 접속하면 자동으로 "/dashboard"로 리다이렉트
	@GetMapping("/")
	public String redirectToDashboard() {
		return "main";
	}

	// "/dashboard" 요청을 처리하고, "templates/dashboard/dashboard.html"을 반환
	@GetMapping("/dashboard")
	public String dashboard(Model model, HttpSession session) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
			return "redirect:/login";
		}

		String adminId = authentication.getName();
		Optional<Admin> adminOptional = adminService.findByAdminId(adminId);
		if (adminOptional.isEmpty()) {
			return "redirect:/login";
		}

		Admin admin = adminOptional.get();
		session.setAttribute("profileImagePath", admin.getProfileImagePath()); // 🔹 세션에 이미지 경로 저장

		List<AdminDTO> stats = adminService.getAdminDTO();
		model.addAttribute("stats", stats);
		return "dashboard";
	}

	// ----------------------------------------------------------------------------------
	// ✅ 회원 & 사업자 통계 추가
	@GetMapping("/stats")
	public ResponseEntity<Map<String, Integer>> getMemberStats() {
		try {
			Map<String, Integer> stats = adminService.getMemberStatistics();
			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyMap());
		}
	}

	// ✅ 새로운 사업자 통계 API 추가
	@GetMapping("/storeStats")
	@ResponseBody
	public ResponseEntity<Map<String, Integer>> getStoreStats() {
		try {
			Map<String, Integer> storeStats = adminService.getStoreStatistics();
			return ResponseEntity.ok(storeStats);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyMap());
		}
	}
	// ----------------------------------------------------------------------------------

	@PostMapping("/admin/updateProfileImage")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateProfileImage(@RequestParam("profileImage") MultipartFile file) {
		Map<String, Object> response = new HashMap<>();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
			response.put("success", false);
			response.put("message", "로그인이 필요합니다.");
			return ResponseEntity.ok(response);
		}

		String adminId = authentication.getName();
		Optional<Admin> adminOptional = adminService.findByAdminId(adminId);

		if (adminOptional.isEmpty()) {
			response.put("success", false);
			response.put("message", "관리자 정보를 찾을 수 없습니다.");
			return ResponseEntity.ok(response);
		}

		Admin admin = adminOptional.get();

		try {
			String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/upload";
			File uploadFolder = new File(uploadDir);
			if (!uploadFolder.exists())
				uploadFolder.mkdirs();

			String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			String uniqueFilename = "change_profile" + fileExtension;
			Path savePath = Paths.get(uploadDir, uniqueFilename);

			Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
			String imageUrl = "/upload/" + uniqueFilename;

			admin.setProfileImagePath(imageUrl);
			adminService.save(admin);

			response.put("success", true);
			response.put("imageUrl", imageUrl);
		} catch (IOException e) {
			response.put("success", false);
			response.put("message", "파일 업로드 중 오류 발생");
		}

		return ResponseEntity.ok(response);
	}

	// ----------------블랙리스트 ---------------------------------------

	// ✅ 불량 사용자 등록
	@PostMapping("/admin/ban")
	public String banUser(@RequestParam String customerId, @RequestParam String reason,
			RedirectAttributes redirectAttributes) {
		// ✅ 회원 정보 조회
		Member member = memberRepository.findById(customerId).orElse(null);
		if (member == null) {
			redirectAttributes.addFlashAttribute("error", "해당 회원을 찾을 수 없습니다.");
			return "redirect:/admin/customerList";
		}

		// ✅ 불량 사용자 DB에 저장
		BlacklistedMember blacklistedMember = new BlacklistedMember();
		blacklistedMember.setCustomerId(member.getCustomerId());
		blacklistedMember.setCustomerName(member.getCustomerName());
		blacklistedMember.setCustomerEmail(member.getCustomerEmail());
		blacklistedMember.setBannedAt(LocalDateTime.now());
		blacklistedMember.setReason(reason);

		blacklistedMemberRepository.save(blacklistedMember);

		redirectAttributes.addFlashAttribute("success", "회원이 불량 사용자로 등록되었습니다.");
		return "redirect:/admin/blackcustomerList";
	}

	// ✅ 불량 사용자 해제
	@PostMapping("/admin/unban")
	public String unbanUser(@RequestParam String customerId, RedirectAttributes redirectAttributes) {
		BlacklistedMember blacklistedMember = blacklistedMemberRepository.findById(customerId).orElse(null);
		if (blacklistedMember == null) {
			redirectAttributes.addFlashAttribute("error", "해당 사용자는 불량 사용자 목록에 없습니다.");
			return "redirect:/admin/blackcustomerList";
		}

		// ✅ 불량 사용자 정보 삭제
		blacklistedMemberRepository.delete(blacklistedMember);

		redirectAttributes.addFlashAttribute("success", "회원이 정상적으로 복구되었습니다.");
		return "redirect:/admin/blackcustomerList";
	}

	// ✅ 불량 사용자 목록 조회
	@GetMapping("/admin/blackcustomerList")
	public String getBlacklistedUsers(Model model) {
		List<BlacklistedMember> blacklistedMembers = blacklistedMemberRepository.findAll();
		model.addAttribute("blacklistedMembers", blacklistedMembers);
		return "admin/blackcustomerList";
	}

	// ---------------- 블랙리스트 (사업자) ---------------------------------------

	// ✅ 불량 사업자 등록
	@PostMapping("/admin/banStore")
	public String banStore(@RequestParam Long storeNo, @RequestParam String reason,
			RedirectAttributes redirectAttributes) {
		// ✅ 사업자 정보 조회
		Store store = storeRepository.findById(storeNo).orElse(null);
		if (store == null) {
			redirectAttributes.addFlashAttribute("error", "해당 사업자를 찾을 수 없습니다.");
			return "redirect:/admin/storeList";
		}

		// ✅ 불량 사업자 DB에 저장
		BlacklistedStore blacklistedStore = new BlacklistedStore();
		blacklistedStore.setStoreNo(store.getStoreNo());
		blacklistedStore.setStoreName(store.getStoreName());
		blacklistedStore.setStoreEmail(store.getStoreEmail());
		blacklistedStore.setStorePhone(store.getStorePhone());
		blacklistedStore.setStoreBusinessNumber(store.getStoreBusinessNumber());
		blacklistedStore.setBannedAt(LocalDateTime.now());
		blacklistedStore.setReason(reason);

		blacklistedStoreRepository.save(blacklistedStore);

		redirectAttributes.addFlashAttribute("success", "사업자가 불량 사업자로 등록되었습니다.");
		return "redirect:/admin/blackstoreList";
	}

	// ✅ 불량 사업자 해제
	@PostMapping("/admin/unbanStore")
	public String unbanStore(@RequestParam Long storeNo, RedirectAttributes redirectAttributes) {
		BlacklistedStore blacklistedStore = blacklistedStoreRepository.findById(storeNo).orElse(null);
		if (blacklistedStore == null) {
			redirectAttributes.addFlashAttribute("error", "해당 사업자는 불량 사업자 목록에 없습니다.");
			return "redirect:/admin/blackstoreList";
		}

		// ✅ 불량 사업자 정보 삭제
		blacklistedStoreRepository.delete(blacklistedStore);

		redirectAttributes.addFlashAttribute("success", "사업자가 정상적으로 복구되었습니다.");
		return "redirect:/admin/blackstoreList";
	}

	// ✅ 불량 사업자 목록 조회
	@GetMapping("/admin/blackstoreList")
	public String getBlacklistedStores(Model model) {
		List<BlacklistedStore> blacklistedStores = blacklistedStoreRepository.findAll();
		model.addAttribute("blacklistedStores", blacklistedStores);
		return "admin/blackstoreList";
	}

}
