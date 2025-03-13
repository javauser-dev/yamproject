package com.yam.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yam.customer.member.domain.Member;
import com.yam.store.Store;

@Repository
public interface SidebarRepository extends JpaRepository<Member, String> {

	// ✅ 블랙리스트에 없는 회원만 조회
	@Query("SELECT m FROM Member m WHERE m.customerId NOT IN (SELECT b.customerId FROM BlacklistedMember b)")
	List<Member> findAllNonBlacklistedMembers();

	// ✅ 블랙리스트에 없는 사업자만 조회
	@Query("SELECT s FROM Store s WHERE s.storeNo NOT IN (SELECT b.storeNo FROM BlacklistedStore b)")
	List<Store> findAllNonBlacklistedStores();
}
