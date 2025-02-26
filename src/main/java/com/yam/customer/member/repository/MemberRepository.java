package com.yam.customer.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> { // <Entity, ID 타입>
	//boolean existsByCustomerNickname(String customerNickname);
	Member findByCustomerNickname(String customerNickname); // 추가
}