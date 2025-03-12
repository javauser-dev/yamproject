package com.yam.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yam.customer.member.domain.Member;
import com.yam.customer.member.domain.WithdrawnMember;
import com.yam.customer.member.repository.MemberRepository;
import com.yam.customer.member.repository.WithdrawnMemberRepository;
import com.yam.store.Store;
import com.yam.store.repository.StoreRepository;

@Service
public class SidebarService {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private WithdrawnMemberRepository withdrawnMemberRepository;

	public List<Member> getAllCustomers() {
		return memberRepository.findAll();
	}

	public List<Store> getAllStores() {
		return storeRepository.findAll();
	}

	public List<WithdrawnMember> getAllWithdrawnCustomer() {
		return withdrawnMemberRepository.findAll();
	}

}
