package com.yam.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.admin.service.SidebarService;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.domain.WithdrawnMember;
import com.yam.store.Store;

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

	@GetMapping("/stores")
	public String getStoreList(Model model) {
		List<Store> stores = sidebarService.getAllStores();
		model.addAttribute("stores", stores);
		return "admin/storeList";
	}

	@GetMapping("/withdrawncustomers")
	public String getWithdrawnCustomerList(Model model) {
		List<WithdrawnMember> withdrawnMembers = sidebarService.getAllWithdrawnCustomer();
		model.addAttribute("withdrawnMembers", withdrawnMembers);
		return "admin/withdrawncustomerList";
	}
}
