package com.yam.customer.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> { // <Entity, ID 타입>
    boolean existsByCustomerId(String customerId); // 아이디 중복 확인
}