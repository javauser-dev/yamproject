package com.yam.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/main")
	public String getMainPage(Model model) {
		return "/main"; // Thymeleaf에서 qna/qnaList.html을 찾아 렌더링
	}
}
