package com.yam.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.admin.dto.AdminDTO;
import com.yam.admin.service.AdminService;

@Controller
@RequestMapping("/")
public class AdminController {

	@Autowired
	private AdminService adminService;

	// 애플리케이션 실행 시 기본 경로로 접속하면 자동으로 "/dashboard"로 리다이렉트
	@GetMapping("/")
	public String redirectToDashboard() {
		return "main";
	}

	

	// "/dashboard" 요청을 처리하고, "templates/dashboard/dashboard.html"을 반환
	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		List<AdminDTO> stats = adminService.getAdminDTO();
		model.addAttribute("stats", stats);
		return "dashboard/dashboard"; // "templates/dashboard/dashboard.html"을 의미
	}

}
