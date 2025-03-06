package com.yam.store.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.yam.store.Store;

@Controller
public class StoreViewController {

	@GetMapping("store/mypage")
	public String myPage(Model model, @AuthenticationPrincipal Store store) {
		model.addAttribute("store", store); // 현재 로그인한 사업자 정보 전달
		return "store/mypage";
	}

	@GetMapping("/edit")
	public String edit(Model model, @AuthenticationPrincipal Store store) {
		model.addAttribute("store", store); // 현재 로그인한 사업자 정보 전달
		return "store/edit";
	}

	@GetMapping("/store/store")
	public String registerPage() {
		return "store/store"; // 🔥 templates/store/store-register.html 반환
	}
}