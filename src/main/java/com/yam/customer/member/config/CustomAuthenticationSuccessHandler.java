package com.yam.customer.member.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final SimpleUrlAuthenticationSuccessHandler defaultSuccessHandler = new SimpleUrlAuthenticationSuccessHandler("/customer/myPage");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("BAN_CUSTOMER")) {
                response.sendRedirect("/admin/info/banCustomer"); // 리디렉션
                return;
            }
        }

        defaultSuccessHandler.onAuthenticationSuccess(request, response, authentication);
    }
}