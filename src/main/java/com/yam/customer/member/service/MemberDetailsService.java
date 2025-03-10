package com.yam.customer.member.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yam.customer.member.domain.CustomUserDetails;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String customerId) throws UsernameNotFoundException {
		Optional<Member> memberOpt = memberRepository.findByCustomerIdEquals(customerId);

		if (memberOpt.isEmpty()) {
			log.error("❌ 회원을 찾을 수 없음: {}", customerId);
			throw new UsernameNotFoundException("해당 회원을 찾을 수 없습니다: " + customerId);
		}

		Member member = memberOpt.get();
		log.info("✅ 로그인한 사용자: {}", member.getCustomerId());

		return new CustomUserDetails(member);
	}
}