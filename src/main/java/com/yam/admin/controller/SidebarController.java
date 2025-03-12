package com.yam.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.admin.service.SidebarService;
import com.yam.customer.member.domain.Member;

@Controller
@RequestMapping("/admin/sidebar")
public class SidebarController {

	@Autowired
	private SidebarService sidebarService;

	// ✅ 회원 목록 조회
	@GetMapping("/customers")
	public String getCustomerList(Model model) {
		List<Member> customers = sidebarService.getAllCustomers();
		model.addAttribute("customers", customers);
		return "admin/customerList"; // customerList.html로 이동
	}
}
