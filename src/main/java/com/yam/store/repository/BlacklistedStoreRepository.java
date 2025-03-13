package com.yam.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yam.store.BlacklistedStore;

@Repository
public interface BlacklistedStoreRepository extends JpaRepository<BlacklistedStore, Long> {

	boolean existsByStoreEmail(String storeEmail); // ✅ 이메일 존재 여부 확인

	// 블랙리스트 사업자 ID 목록 가져오기
	@Query("SELECT b.storeNo FROM BlacklistedStore b")
	List<Long> findAllStoreNos();

	// 블랙리스트에서 사업자 정보 삭제 (벤 해제)
	void deleteByStoreNo(Long storeNo);
}
