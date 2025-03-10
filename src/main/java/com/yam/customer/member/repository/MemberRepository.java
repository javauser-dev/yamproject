package com.yam.customer.member.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> { // <Entity, ID 타입>
	// boolean existsByCustomerNickname(String customerNickname);
	Member findByCustomerNickname(String customerNickname); // 추가

	// ID로 검색 (부분 일치)
	Page<Member> findByCustomerIdContaining(String searchKeyword, Pageable pageable);

	// 닉네임으로 검색 (부분 일치)
	Page<Member> findByCustomerNicknameContaining(String searchKeyword, Pageable pageable);

	// ID 또는 닉네임으로 검색 (부분 일치)
	Page<Member> findByCustomerIdContainingOrCustomerNicknameContaining(String idKeyword, String nicknameKeyword,
			Pageable pageable);

	Optional<Member> findByCustomerIdEquals(String Id);

	// 메일로 회원 존재 여부 확인
	boolean existsByCustomerNickname(String customerEmail);

	Optional<Member> findByCustomerId(String customerEmail);

}