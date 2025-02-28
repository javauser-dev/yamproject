package com.yam.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterController {

	// ✅ 회원가입 선택 페이지
	@GetMapping("/register/select")
	public String selectRegister() {
		return "register/register-select"; // templates/register/register-select.html을 반환
	}

	// ✅ 개인 회원가입 페이지
	@GetMapping("/register/personal")
	public String personalRegister() {
		return "register/personal-register"; // templates/register/personal-register.html을 반환
	}

	// ✅ 사업자 회원가입 페이지
	@GetMapping("/register/business")
	public String businessRegister() {
		return "register/business-register"; // templates/register/business-register.html을 반환
	}
}
