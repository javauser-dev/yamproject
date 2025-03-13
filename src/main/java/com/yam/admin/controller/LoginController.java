package com.yam.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yam.admin.model.Admin;
import com.yam.admin.repository.AdminRepository;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.BlacklistedMemberRepository;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.store.Store;
import com.yam.store.repository.BlacklistedStoreRepository;
import com.yam.store.repository.StoreRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private BlacklistedMemberRepository blacklistedMemberRepository;
	@Autowired
	private BlacklistedStoreRepository blacklistedStoreRepository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping("/login")
	public String loginPage(@RequestParam(required = false) Boolean error, Model model) {
		if (Boolean.TRUE.equals(error)) {
			model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
		}
		return "login";
	}

	@PostMapping("/api/login")
	public ResponseEntity<?> apiLogin(@RequestParam String id, String StoreEmail, @RequestParam String password,
			HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		// ✅ 불량 사용자 체크
		if (blacklistedMemberRepository.existsByCustomerId(id)) {
			response.put("success", false);
			response.put("message", "이 계정은 관리자에 의해 정지되었습니다. 고객센터로 문의하세요.");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}

		// ✅ 관리자 로그인
		Optional<Admin> adminOpt = adminRepository.findByIdEquals(id);
		if (adminOpt.isPresent() && adminOpt.get().getPassword().equals(password)) {
			Admin admin = adminOpt.get();
			session.setAttribute("userRole", "ADMIN");
			session.setAttribute("adminId", admin.getId());
			session.setAttribute("adminNo", admin.getNo());
			session.setAttribute("adminName", admin.getName());

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(admin.getId(),
					password, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			response.put("success", true);
			response.put("role", "ADMIN");
			response.put("redirect", "/dashboard");
			return ResponseEntity.ok(response);
		}

		// ✅ 일반 회원 로그인
		Optional<Member> userOpt = memberRepository.findByCustomerIdEquals(id);
		if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getCustomerPassword())) {
			Member member = userOpt.get();
			session.setAttribute("userRole", "CUSTOMER");
			session.setAttribute("customerId", member.getCustomerId());

			String userRole = member.getCustomerRole();
			if (userRole == null || userRole.isEmpty()) {
				userRole = "ROLE_CUSTOMER"; // 기본값 설정
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					member.getCustomerId(), password, List.of(new SimpleGrantedAuthority(userRole)));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

			response.put("success", true);
			response.put("role", "CUSTOMER");
			response.put("redirect", "/customer/mypage");
			return ResponseEntity.ok(response);
		}

		// ✅ 사업자 로그인
		Optional<Store> storeOpt = storeRepository.findByStoreEmail(id);
		if (storeOpt.isPresent()) {
			Store store = storeOpt.get();
			if (blacklistedStoreRepository.existsByStoreEmail(store.getStoreEmail())) {
				response.put("success", false);
				response.put("message", "이 계정은 관리자에 의해 정지되었습니다. 고객센터로 문의하세요.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
			}
		}
		if (storeOpt.isPresent() && passwordEncoder.matches(password, storeOpt.get().getStorePassword())) {
			Store store = storeOpt.get();
			session.setAttribute("userRole", "STORE");
			session.setAttribute("storeId", store.getStoreEmail());

			String storeRole = store.getStoreRole();
			if (storeRole == null || storeRole.isEmpty()) {
				storeRole = "ROLE_CUSTOMER"; // 기본값 설정
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					store.getStoreEmail(), password, List.of(new SimpleGrantedAuthority("ROLE_STORE")));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			session.setAttribute("loggedInStore", store); // ✅ 로그인된 Store 객체 저장

			response.put("success", true);
			response.put("role", "STORE");
			response.put("redirect", "store/mypage");
			return ResponseEntity.ok(response);
		}

		response.put("success", false);
		response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	// ✅ 네이버 로그인 처리
	@PostMapping("/api/naver-login")
	public ResponseEntity<?> naverLogin(@RequestParam String email, @RequestParam String name,
			@RequestParam String birthDate, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		// ✅ 기존 회원 확인 (이메일 기준)
		Optional<Member> existingMember = memberRepository.findByCustomerId(email);
		if (existingMember.isPresent()) {
			Member member = existingMember.get();

			// ✅ 세션 저장
			session.setAttribute("userRole", "CUSTOMER");
			session.setAttribute("customerId", member.getCustomerId());

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					member.getCustomerId(), null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

			response.put("success", true);
			response.put("role", "CUSTOMER");
			response.put("redirect", "/customer/mypage");
			return ResponseEntity.ok(response);
		}

		// ✅ 신규 회원 가입이 필요한 경우 (회원가입 페이지로 이동)
		response.put("success", false);
		response.put("redirect",
				"/customer/signup?email=" + email + "&name=" + name + "&birthDate=" + birthDate + "&fromNaver=true");
		return ResponseEntity.ok(response);
	}
}
