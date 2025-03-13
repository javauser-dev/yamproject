package com.yam.customer.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yam.customer.member.domain.BlacklistedMember;

@Repository
public interface BlacklistedMemberRepository extends JpaRepository<BlacklistedMember, String> {

	// 특정 ID가 블랙리스트에 있는지 확인
	boolean existsByCustomerId(String customerId);

	// 블랙리스트 ID 목록 가져오기
	@Query("SELECT b.customerId FROM BlacklistedMember b")
	List<String> findAllCustomerIds();

	// 블랙리스트에서 회원 정보 삭제 (벤 해제)
	void deleteByCustomerId(String customerId);
}
