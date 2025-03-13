package com.yam.auth.Controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yam.auth.dto.LoginRequest;
import com.yam.auth.dto.LoginResponse;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;

	// ✅ 로그인 API
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
		Optional<Member> memberOptional = memberService.findById(request.getCustomerId());

		if (memberOptional.isEmpty()) {
			return ResponseEntity.badRequest().body("아이디가 존재하지 않습니다.");
		}

		Member member = memberOptional.get();

		// 비밀번호 검증
		if (!passwordEncoder.matches(request.getCustomerPassword(), member.getCustomerPassword())) {
			return ResponseEntity.badRequest().body("비밀번호가 올바르지 않습니다.");
		}

		// 세션 저장
		session.setAttribute("customerId", member.getCustomerId());
		session.setAttribute("nickname", member.getCustomerNickname());

		// 로그인 성공 응답
		LoginResponse response = new LoginResponse(member.getCustomerId(), member.getCustomerNickname());
		return ResponseEntity.ok(response);
	}
}