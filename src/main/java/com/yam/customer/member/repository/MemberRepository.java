package com.yam.customer.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yam.customer.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> { // <Entity, ID 타입>
	// boolean existsByCustomerNickname(String customerNickname);
	Member findByCustomerNickname(String customerNickname); // 추가

	Optional<Member> findByCustomerIdEquals(String customerId);

}