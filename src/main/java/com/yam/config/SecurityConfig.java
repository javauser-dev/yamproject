package com.yam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.yam.config.oauth.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final CustomOAuth2UserService customOAuth2UserService;

	// ✅ @Lazy 추가 (순환 의존성 방지)
	public SecurityConfig(@Lazy UserDetailsService userDetailsService,
			CustomOAuth2UserService customOAuth2UserService) {
		this.userDetailsService = userDetailsService;
		this.customOAuth2UserService = customOAuth2UserService;
	}

	// ✅ 비밀번호 암호화 빈 등록
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// ✅ Spring Security 필터 체인 설정
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())

				// ✅ 권한 설정
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/api/signup", "/customer/signup", "/store/signup", "/store/register",
								"/customer/checkId", "/customer/checkNickname", "/customer/sendVerificationCode",
								"/customer/verifyCode", "/login/**", "/customer/signup-success")
						.permitAll()

						.requestMatchers("/admin/**", "/dashboard/**").hasAuthority("ROLE_ADMIN")

						.requestMatchers("/store/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE")

						// ✅ storeCommunity 페이지는 ROLE_ADMIN과 ROLE_STORE만 접근 가능하도록 설정
						.requestMatchers("/storeCommunity/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE")

						.requestMatchers("/customer/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CUSTOMER")

						.requestMatchers(new AntPathRequestMatcher("/customer/reserve/**")).authenticated()

						.anyRequest().permitAll())

				.formLogin(formLogin -> formLogin.loginPage("/login").usernameParameter("customerId")
						.passwordParameter("customerPassword").defaultSuccessUrl("/customer/mypage").permitAll())

				.oauth2Login(oauth2 -> oauth2.loginPage("/login").defaultSuccessUrl("/customer/mypage", true)
						.failureUrl("/customer/signup")
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))

				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true)
						.deleteCookies("JSESSIONID").permitAll())
				.rememberMe(rememberMe -> rememberMe.key("uniqueAndSecret") // ✅ 쿠키 보안 키
						.tokenValiditySeconds(86400) // ✅ 24시간 동안 로그인 유지
						.userDetailsService(userDetailsService))

				.securityContext(securityContext -> securityContext.requireExplicitSave(false))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

				.userDetailsService(userDetailsService);

		return http.build();
	}
}
