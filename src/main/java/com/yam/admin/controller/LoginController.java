package com.yam.admin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	@Autowired // 🔥 이 부분이 반드시 있어야 함
	private MemberRepository memberRepository;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private StoreRepository storeRepository;

	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
		
		
		//admin
		Optional<Admin> adminOpt = adminRepository.findByIdEquals(id);
		if (adminOpt.isPresent() && adminOpt.get().getPassword().equals(password)) { // 🔥 암호화 안된 비밀번호 비교
			session.setAttribute("userRole", "ADMIN");
			session.setAttribute("userId", adminOpt.get().getId());
			session.setAttribute("userNo", adminOpt.get().getNo());

			response.put("success", true);
			response.put("role", "ADMIN");
			response.put("userId", adminOpt.get().getId());
			return ResponseEntity.ok(response);
		}

		// 2. Member 계정 확인 (암호화됨, matches() 사용)
		Optional<Member> userOpt = memberRepository.findByCustomerIdEquals(id);
		if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getCustomerPassword())) { // 🔥 암호화된
																												// 비밀번호
																												// 비교
			session.setAttribute("customerRole", "CUSTOMER");
			session.setAttribute("customerId", userOpt.get().getCustomerId());

			response.put("success", true);
			response.put("role", "CUSTOMER");
			response.put("userId", userOpt.get().getCustomerId());
			return ResponseEntity.ok(response);
		}


		Optional<Store> storeOpt = storeRepository.findByEmailEquals(id);
		if (storeOpt.isPresent() && passwordEncoder.matches(password, storeOpt.get().getPassword())) { // 🔥 암호화된
																										// 비밀번호
																										// 비교
			session.setAttribute("storerRole", "STORE");
			session.setAttribute("storeId", storeOpt.get().getEmail());

			response.put("success", true);
			response.put("role", "STORE");
			response.put("storeId", storeOpt.get().getEmail());
			return ResponseEntity.ok(response);
		}

		response.put("success", false);
		response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
}
