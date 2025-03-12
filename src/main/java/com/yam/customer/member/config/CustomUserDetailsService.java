package com.yam.customer.member.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yam.customer.member.domain.CustomUserDetails;
import com.yam.customer.member.domain.Member;
import com.yam.customer.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(member);
    }*/
	
	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // CustomUserDetails 생성 시 Member 객체와 함께 기본 권한("ROLE_USER")을 부여
        return new CustomUserDetails(member, "UNBAN_CUSTOMER"); // 기본 권한 추가
    }
}
