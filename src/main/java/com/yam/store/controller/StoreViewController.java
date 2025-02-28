package com.yam.store.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.yam.store.Store;

@Controller
public class StoreViewController {
    
	 @GetMapping("/")
	    public String home() {
	        return "index"; // "templates/index.html" 반환
	    }
	 
    @GetMapping("/mypage")
    public String myPage(Model model, @AuthenticationPrincipal Store store) {
        model.addAttribute("store", store); // 현재 로그인한 사업자 정보 전달
        return "store/mypage";
    }
    
    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal Store store) {
        model.addAttribute("store", store); // 현재 로그인한 사업자 정보 전달
        return "store/edit";
    }
    
    @GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal Principal principal) {
        if (principal != null) {
            return "redirect:/"; // ✅ 이미 로그인된 사용자는 홈으로 리디렉션
        }
        return "login"; // ✅ 로그인 페이지 반환 (Thymeleaf 템플릿 경로 확인)
    }

    @GetMapping("/store/store")
    public String registerPage() {
        return "store/store"; // 🔥 templates/store/store-register.html 반환
    }
}