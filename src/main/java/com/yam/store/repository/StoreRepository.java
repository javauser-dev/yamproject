package com.yam.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.store.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
	Store findByVerificationToken(String token);

	Optional<Store> findByStoreEmail(String storeEmail);

	boolean existsByStoreEmail(String storeEmail); // ✅ 추가된 코드

	Optional<Store> findByStoreBusinessNumber(String storeBusinessNumber);

	Optional<Store> findByStoreEmailEquals(String storeEmail);
}