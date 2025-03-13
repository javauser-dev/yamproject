package com.yam.config.oauth;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {
	private final OAuth2User oAuth2User;
	private final String role;

	public CustomOAuth2User(OAuth2User oAuth2User, String role) {
		this.oAuth2User = oAuth2User;
		this.role = role;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return oAuth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getName() {
		return oAuth2User.getName();
	}
}
