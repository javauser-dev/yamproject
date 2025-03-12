package com.yam.admin.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

import com.yam.admin.dto.AdminDTO;
import com.yam.admin.model.Admin;
import com.yam.admin.service.AdminService;
import com.yam.store.service.StoreService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private StoreService storeService; // ✅ 사업자 서비스 추가

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

}
