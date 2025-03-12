package com.yam.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.store.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {

	Store findByVerificationToken(String token);

	Optional<Store> findByStoreEmail(String storeEmail); // ✅ 이메일 필드가 storeEmail이므로 이 메서드만 유지

	boolean existsByStoreEmail(String storeEmail); // ✅ 존재 여부 확인

	Optional<Store> findByStoreBusinessNumber(String storeBusinessNumber);

}

// ❌ 잘못된 메서드 삭제
// Optional<Store> findByEmailEquals(String storeEmail);
// Optional<Store> findByStoreEmailEquals(String storeEmail);
