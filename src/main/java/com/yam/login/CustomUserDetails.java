package com.yam.login;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.yam.store.Store;

public class CustomUserDetails implements UserDetails {
    private final Store store; // Store 엔티티

    public CustomUserDetails(Store store) {
        this.store = store;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한이 필요하면 여기에 추가
    }

    @Override
    public String getPassword() {
        return store.getPassword();
    }

    @Override
    public String getUsername() {
        return store.getEmail(); // 이메일을 아이디로 사용
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
        return store.isEnabled(); // 이메일 인증된 계정만 활성화
    }

    public Store getStore() {
        return store;
    }
}