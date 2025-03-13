package com.yam.customer.member.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
public class CustomUserDetails implements UserDetails, Serializable { // ✅ Serializable 추가

	private static final long serialVersionUID = 1L; // ✅ 직렬화 버전 ID 추가

	private final Member member;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String role = member.getCustomerRole();
		if (role == null || role.isEmpty()) {
			role = "ROLE_USER"; // 기본값 설정
		}
		log.info("✅ 사용자 권한: {}", role);
		return List.of(new SimpleGrantedAuthority(role));
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
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return "Y".equals(member.getCustomerApproval());
	}
}
