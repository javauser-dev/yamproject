package com.yam.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yam.admin.service.SidebarService;
import com.yam.customer.member.domain.BlacklistedMember;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.domain.WithdrawnMember;
import com.yam.store.BlacklistedStore;
import com.yam.store.Store;
import com.yam.store.WithdrawnStore;

@Controller
@RequestMapping("/admin/sidebar")
public class SidebarController {

	@Autowired
	private SidebarService sidebarService;

	// ✅ 블랙리스트 제외한 일반 회원 목록 조회
	@GetMapping("/customers")
	public String getCustomerList(Model model) {
		List<Member> customers = sidebarService.getAllNonBlacklistedCustomers(); // 블랙리스트 제외
		model.addAttribute("customers", customers);
		return "admin/customerList"; // customerList.html로 이동
	}

	// ✅ 사업자 목록 조회
	@GetMapping("/stores")
	public String getStoreList(Model model) {
		List<Store> stores = sidebarService.getAllNonBlacklistedStores();
		model.addAttribute("stores", stores);
		return "admin/storeList";
	}

	// ✅ 탈퇴 회원 목록 조회
	@GetMapping("/withdrawncustomers")
	public String getWithdrawnCustomerList(Model model) {
		List<WithdrawnMember> withdrawnMembers = sidebarService.getAllWithdrawnCustomer();
		model.addAttribute("withdrawnMembers", withdrawnMembers);
		return "admin/withdrawncustomerList";
	}

	// ✅ 탈퇴 사업자 목록 조회
	@GetMapping("/withdrawnstores")
	public String getWithdrawnStoreList(Model model) {
		List<WithdrawnStore> withdrawnStores = sidebarService.getAllWithdrawnStore();
		model.addAttribute("withdrawnStores", withdrawnStores);
		return "admin/withdrawnstoreList";
	}

	// ✅ 블랙리스트 회원 목록 조회
	@GetMapping("/blackcustomers")
	public String getBlacklistedCustomers(Model model) {
		List<BlacklistedMember> blacklistedMembers = sidebarService.getAllBlacklistedMembers();
		model.addAttribute("blacklistedMembers", blacklistedMembers);
		return "admin/blackcustomerList"; // 블랙리스트 회원 목록 페이지
	}

	// ✅ 불량 사업자 목록 조회
	@GetMapping("/blackstores")
	public String getBlacklistedStores(Model model) {
		List<BlacklistedStore> blacklistedStores = sidebarService.getAllBlacklistedStores();
		model.addAttribute("blacklistedStores", blacklistedStores);
		return "admin/blackstoreList";
	}

}
