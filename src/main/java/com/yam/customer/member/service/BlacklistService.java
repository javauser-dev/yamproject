package com.yam.customer.member.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yam.customer.member.domain.BlacklistedMember;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.BlacklistedMemberRepository;
import com.yam.customer.member.repository.MemberRepository;

@Service
public class BlacklistService {

	@Autowired
	private BlacklistedMemberRepository blacklistedMemberRepository;

	@Autowired
	private MemberRepository memberRepository;

	// ✅ 불량 사용자 등록
	@Transactional
	public void banMember(String customerId, String reason) {
		Member member = memberRepository.findById(customerId).orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

		BlacklistedMember blacklistedMember = new BlacklistedMember(member.getCustomerId(), member.getCustomerName(),
				member.getCustomerEmail(), LocalDateTime.now(), reason);
		blacklistedMemberRepository.save(blacklistedMember);
	}

	// ✅ 불량 사용자 해제
	@Transactional
	public void unbanMember(String customerId) {
		BlacklistedMember blacklistedMember = blacklistedMemberRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("해당 사용자가 블랙리스트에 없습니다."));

		// 원래 회원으로 복구
		Member restoredMember = new Member(blacklistedMember.getCustomerId(), "default_password", // 🔹 기본 비밀번호 설정 필요
																									// (혹은 이전 정보 복구)
				"닉네임", blacklistedMember.getCustomerName(), blacklistedMember.getCustomerEmail(),
				LocalDateTime.now().toLocalDate(), "M", "승인", "/images/default-profile.png", LocalDateTime.now(),
				"ROLE_CUSTOMER", false);

		memberRepository.save(restoredMember);
		blacklistedMemberRepository.deleteByCustomerId(customerId); // ✅ 블랙리스트에서 제거
	}
}
