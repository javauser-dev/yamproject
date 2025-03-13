package com.yam.store.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yam.store.BlacklistedStore;
import com.yam.store.Store;
import com.yam.store.repository.BlacklistedStoreRepository;
import com.yam.store.repository.StoreRepository;

@Service
public class BlacklistedStoreService {

	@Autowired
	private BlacklistedStoreRepository blacklistedStoreRepository;

	@Autowired
	private StoreRepository storeRepository;

	// ✅ 사업자를 불량 사업자로 등록
	public boolean banStore(Long storeNo, String reason) {
		Store store = storeRepository.findById(storeNo).orElse(null);
		if (store == null) {
			return false; // 존재하지 않는 사업자
		}

		BlacklistedStore blacklistedStore = new BlacklistedStore();
		blacklistedStore.setStoreNo(store.getStoreNo());
		blacklistedStore.setStoreName(store.getStoreName());
		blacklistedStore.setStoreBusinessNumber(store.getStoreBusinessNumber());
		blacklistedStore.setStoreEmail(store.getStoreEmail());
		blacklistedStore.setStorePhone(store.getStorePhone());
		blacklistedStore.setBannedAt(LocalDateTime.now());
		blacklistedStore.setReason(reason);

		blacklistedStoreRepository.save(blacklistedStore);
		return true;
	}

	// ✅ 불량 사업자 해제
	public boolean unbanStore(String storeEmail) {
		if (!blacklistedStoreRepository.existsByStoreEmail(storeEmail)) {
			return false; // 이미 블랙리스트에 없음
		}
		blacklistedStoreRepository.existsByStoreEmail(storeEmail);
		return true;
	}

	// ✅ 불량 사업자 목록 조회
	public List<BlacklistedStore> getBlacklistedStores() {
		return blacklistedStoreRepository.findAll();
	}
}
