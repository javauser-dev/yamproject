package com.yam.store.email.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yam.store.email.service.EmailService2;

@RestController
@RequestMapping("/email")
public class EmailVerificationController {

	@Autowired
	private EmailService2 emailService;

	// ✅ 인증 코드 요청
	@PostMapping("/send-verification-email")
	public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
		String email = request.get("storeEmail");

		if (email == null || email.isEmpty()) {
			return ResponseEntity.badRequest().body("이메일을 입력하세요!");
		}

		emailService.sendVerificationEmail(email);
		return ResponseEntity.ok("인증 메일 전송 완료");
	}

	// ✅ 인증 코드 검증
	@PostMapping("/verify-code")
	public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> request) {
		String email = request.get("storeEmail");
		String code = request.get("code");

		if (email == null || code == null) {
			return ResponseEntity.badRequest().body("Invalid request data");
		}

		if (emailService.verifyCode(email, code)) {
			return ResponseEntity.ok("success");
		} else {
			return ResponseEntity.badRequest().body("fail");
		}
	}
}