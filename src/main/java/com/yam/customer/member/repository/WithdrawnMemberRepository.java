package com.yam.customer.member.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yam.customer.member.domain.WithdrawnMember;

public interface WithdrawnMemberRepository extends JpaRepository<WithdrawnMember, String>{
	void deleteByWithdrawalCompletedAtBefore(LocalDateTime dateTime);
	void deleteByWithdrawalCompletedAtBetween(LocalDateTime start, LocalDateTime end); // 추
}
