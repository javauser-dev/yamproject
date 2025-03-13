package com.yam.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yam.customer.member.repository.BlacklistedMemberRepository;
import com.yam.store.repository.BlacklistedStoreRepository;

@Service
public class LoginService {

	@Autowired
	private BlacklistedMemberRepository blacklistedMemberRepository;

	@Autowired
	private BlacklistedStoreRepository blacklistedStoreRepository;

	public boolean isBanned(String customerId) {
		return blacklistedMemberRepository.existsByCustomerId(customerId);
	}

	// ✅ 이메일이 블랙리스트에 등록되었는지 확인
	public boolean isBannedStore(String storeEmail) {
		return blacklistedStoreRepository.existsByStoreEmail(storeEmail.trim().toLowerCase());
	}
}
