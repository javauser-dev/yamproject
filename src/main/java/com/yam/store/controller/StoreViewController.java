package com.yam.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.store.Store;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/store")
public class StoreViewController {

	@GetMapping("/mypage")
	public String showMypage(HttpSession session, Model model) {
	    Store store = (Store) session.getAttribute("loggedInStore");

	    if (store == null) {
	        System.out.println("로그인된 store가 없습니다.");
	        return "redirect:/login"; // 로그인 페이지로 리다이렉트
	    }

	    System.out.println("로그인된 store: " + store.getStoreNickname());

	    // 세션에서 storeNickname을 가져와서 모델에 추가
	    model.addAttribute("storeNickname", store.getStoreNickname());

	    // 마이페이지로 이동
	    return "store/mypage"; // Thymeleaf 템플릿 파일 (mypage.html)
	}




	@GetMapping("/edit")
	public String showEditPage(HttpSession session, Model model) {
	    Store store = (Store) session.getAttribute("loggedInStore");

	    // 세션에 store 정보가 있는지 확인
	    if (store == null) {
	        System.out.println("세션에 저장된 store 정보가 없습니다.");
	        return "redirect:/login"; // 로그인 안 된 경우 로그인 페이지로 리다이렉트
	    }

	    System.out.println("Logged in store email: " + store.getStoreEmail());

	    model.addAttribute("storeEmail", store.getStoreEmail());
	    return "store/edit"; // 수정 페이지로 리턴
	}



	@GetMapping("/signup")
	public String registerPage() {
		return "store/signup"; // 🔥 templates/store/store.html 반환
	}
}