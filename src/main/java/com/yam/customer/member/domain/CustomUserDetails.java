package com.yam.customer.member.domain;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

	private final Member member;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return member.getCustomerPassword();
	}

	@Override
	public String getUsername() {
		return member.getCustomerId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 계정 만료 여부 (true: 만료되지 않음)
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // 계정 잠금 여부 (true: 잠기지 않음)
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 비밀번호 만료 여부 (true: 만료되지 않음)
	}

	@Override
	public boolean isEnabled() {
		return member.getCustomerApproval().equals("Y"); // 계정 활성화 여부
	}
}