package com.yam.store.email.controller;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.store.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
	Store findByVerificationToken(String token);

	Optional<Store> findByEmail(String email); // 🔹 이메일로 사용자 조회하는 메서드 추가

	boolean existsByEmail(String email); // ✅ 추가된 코드

	Optional<Store> findByBusinessNumber(String businessNumber);

	Optional<Store> findByEmailEquals(String email);
}