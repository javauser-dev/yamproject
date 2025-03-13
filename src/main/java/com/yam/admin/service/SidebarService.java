package com.yam.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yam.admin.repository.SidebarRepository;
import com.yam.customer.member.domain.BlacklistedMember;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.domain.WithdrawnMember;
import com.yam.customer.member.repository.BlacklistedMemberRepository;
import com.yam.customer.member.repository.WithdrawnMemberRepository;
import com.yam.store.BlacklistedStore;
import com.yam.store.Store;
import com.yam.store.WithdrawnStore;
import com.yam.store.repository.BlacklistedStoreRepository;
import com.yam.store.repository.StoreRepository;
import com.yam.store.repository.WithdrawnStoreRepository;

@Service
public class SidebarService {

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private SidebarRepository sidebarRepository;

	@Autowired
	private WithdrawnMemberRepository withdrawnMemberRepository;
	@Autowired
	private WithdrawnStoreRepository withdrawnStoreRepository;

	@Autowired
	private BlacklistedMemberRepository blacklistedMemberRepository;

	@Autowired
	private BlacklistedStoreRepository blacklistedStoreRepository;

	// ✅ 블랙리스트 제외한 회원 목록 조회
	public List<Member> getAllNonBlacklistedCustomers() {
		return sidebarRepository.findAllNonBlacklistedMembers();
	}

	public List<Store> getAllNonBlacklistedStores() {
		return sidebarRepository.findAllNonBlacklistedStores();
	}

	public List<WithdrawnMember> getAllWithdrawnCustomer() {
		return withdrawnMemberRepository.findAll();
	}

	public List<WithdrawnStore> getAllWithdrawnStore() {
		return withdrawnStoreRepository.findAll();
	}

	// ✅ 블랙리스트 목록 조회
	public List<BlacklistedMember> getAllBlacklistedMembers() {
		return blacklistedMemberRepository.findAll();
	}

	// ✅ 블랙리스트(사업자) 목록 조회
	public List<BlacklistedStore> getAllBlacklistedStores() {
		return blacklistedStoreRepository.findAll();
	}

}
