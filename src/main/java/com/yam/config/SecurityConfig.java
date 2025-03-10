package com.yam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.yam.config.oauth.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final CustomOAuth2UserService customOAuth2UserService; // 🔹 OAuth2 서비스 자동 주입

	// ✅ 비밀번호 암호화 빈 등록
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// ✅ Spring Security 필터 체인 설정
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// ✅ CSRF 보호 비활성화 (API 사용 시 필요, 프론트엔드와 연동할 경우 비활성화)
				.csrf(csrf -> csrf.disable())

				// ✅ 권한 설정
				.authorizeHttpRequests(authz -> authz
						// 🔹 회원가입, 로그인 관련 요청 허용
						.requestMatchers("/api/auth/**", "/login", "/register/**").permitAll()

						// 🔹 관리자 페이지는 ROLE_ADMIN만 접근 가능
						.requestMatchers("/admin/**", "/dashboard/**").hasAuthority("ROLE_ADMIN")

						// 🔹 사업자 페이지는 ROLE_ADMIN, ROLE_STORE 접근 가능
						.requestMatchers("/store/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE")

						// 🔹 고객 페이지는 ROLE_ADMIN, ROLE_CUSTOMER 접근 가능
						.requestMatchers("/customer/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CUSTOMER")

						// 🔹 예약 관련 요청은 인증된 사용자만 접근 가능
						.requestMatchers(new AntPathRequestMatcher("/customer/reserve/**")).authenticated()

						// 🔹 API 요청도 인증 필요 (추후 JWT 적용 가능)
						.requestMatchers("/api/**").authenticated()

						// 🔹 그 외 요청은 모두 허용
						.anyRequest().permitAll())

				// ✅ 로그인 설정 (폼 로그인 활성화)
				.formLogin(formLogin -> formLogin.loginPage("/login") // 로그인 페이지
						.usernameParameter("customerId") // 아이디 파라미터
						.passwordParameter("customerPassword") // 비밀번호 파라미터
						.defaultSuccessUrl("/customer/mypage") // 로그인 성공시 이동할 페이지
						.permitAll())

				// ✅ 카카오,네이버 로그인 추가
				.oauth2Login(oauth2 -> oauth2.loginPage("/login").defaultSuccessUrl("/customer/mypage")
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))) // 📌 자동 주입된 서비스
																										// 사용!

				// ✅ 로그아웃 설정
				.logout(logout -> logout.logoutUrl("/logout") // 로그아웃 URL
						.logoutSuccessUrl("/login") // 로그아웃 성공 후 리다이렉트 URL
						.invalidateHttpSession(true) // 세션 무효화
						.deleteCookies("JSESSIONID") // 쿠키 삭제
						.permitAll())

				// ✅ REST API 기반 로그인 (API 호출을 통해 로그인 수행)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 기존 세션
																												// 유지

				// ✅ 사용자 정보 서비스 설정
				.userDetailsService(userDetailsService);

		return http.build();
	}
}
