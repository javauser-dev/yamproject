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
    private final String defaultRole;

    /*@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 정보 (여기서는 간단하게 ROLE_USER로 설정)
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }*/
    
    /*@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 정보 (MemberRole enum 사용)
        if (member.getMemberRole() != null) {
            return Collections.singletonList(new SimpleGrantedAuthority(member.getMemberRole().name()));
        }
        return Collections.emptyList(); // 권한이 없으면 빈 리스트 반환
    }*/
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // MemberRole이 있으면 그것을 사용, 없으면 기본 권한 사용
        if (member.getMemberRole() != null) {
            return Collections.singletonList(new SimpleGrantedAuthority(member.getMemberRole().name()));
        }
        return Collections.singletonList(new SimpleGrantedAuthority(defaultRole)); // 기본 권한 반환
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