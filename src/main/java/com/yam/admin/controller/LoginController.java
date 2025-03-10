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
import com.yam.customer.member.repository.MemberRepository;
import com.yam.store.Store;
import com.yam.store.email.controller.StoreRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private StoreRepository storeRepository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping("/login")
	public String loginPage(@RequestParam(required = false) Boolean error, Model model) {
		if (Boolean.TRUE.equals(error)) {
			model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
		}
		return "login";
	}

	@PostMapping("/api/login")
	public ResponseEntity<?> apiLogin(@RequestParam String id, @RequestParam String password, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		// 관리자 로그인
		Optional<Admin> adminOpt = adminRepository.findByIdEquals(id);
		if (adminOpt.isPresent() && adminOpt.get().getPassword().equals(password)) {
			Admin admin = adminOpt.get();

			// ✅ 세션에 저장
			session.setAttribute("userRole", "ADMIN");
			session.setAttribute("adminId", admin.getId());
			session.setAttribute("adminNo", admin.getNo());
			session.setAttribute("adminName", admin.getName()); // ✅ 관리자 이름 추가

			// ✅ 로그 확인
			System.out.println("✅ 관리자 로그인 성공: " + admin.getName());
			System.out.println("✅ 세션 값 확인: " + session.getAttribute("adminName"));

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(admin.getId(),
					password, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			response.put("success", true);
			response.put("role", "ADMIN");
			response.put("adminName", admin.getName());
			response.put("redirect", "/dashboard");
			return ResponseEntity.ok(response);
		}

		// 회원 로그인
		Optional<Member> userOpt = memberRepository.findByCustomerIdEquals(id);
		if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getCustomerPassword())) {
			session.setAttribute("userRole", "CUSTOMER");
			session.setAttribute("customerId", userOpt.get().getCustomerId());

			String userRole = userOpt.get().getCustomerRole();
			if (userRole == null || userRole.isEmpty()) {
				userRole = "ROLE_CUSTOMER"; // 기본값 설정
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userOpt.get().getCustomerId(), password, List.of(new SimpleGrantedAuthority(userRole)));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext()); // 🔹 보장

			response.put("success", true);
			response.put("role", "CUSTOMER");
			response.put("redirect", "/customer/mypage");
			return ResponseEntity.ok(response);
		}

		// 사업자 로그인
		Optional<Store> storeOpt = storeRepository.findByEmailEquals(id);
		if (storeOpt.isPresent() && passwordEncoder.matches(password, storeOpt.get().getPassword())) {
			session.setAttribute("userRole", "STORE"); // ✅ 추가
			session.setAttribute("storeId", storeOpt.get().getEmail());

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					storeOpt.get().getEmail(), password, List.of(new SimpleGrantedAuthority("ROLE_STORE")));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

			response.put("success", true);
			response.put("role", "STORE");
			response.put("redirect", "/main");
			return ResponseEntity.ok(response);
		}

		response.put("success", false);
		response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

}
