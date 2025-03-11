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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	   private final UserDetailsService userDetailsService;

	    public SecurityConfig(@Lazy UserDetailsService userDetailsService) {
	        this.userDetailsService = userDetailsService;
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 PasswordEncoder
	    }

	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
	            .authorizeHttpRequests(authz -> authz
	                .requestMatchers("/api/signup", "/customer/signup", "/store/signup", "/store/register", "/store/edit", "/store/update", "/shop/shopDetail/**")
	                .permitAll() // 특정 URL은 인증 없이 접근 가능
	                .requestMatchers("/admin/**", "/dashboard/**").hasAuthority("ROLE_ADMIN") // 관리자만 접근 가능한 URL
	                .requestMatchers("/customer/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CUSTOMER") // 고객과 관리자만 접근
	                .requestMatchers("/store/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE") // 고객과 관리자만 접근
	                .anyRequest().permitAll()) // 나머지 URL은 모두 허용
	            .formLogin(form -> form
	            	    .loginPage("/login") // 커스텀 로그인 페이지
	            	    .defaultSuccessUrl("/main") // 로그인 성공 시 리다이렉트
	            	    .failureUrl("/login?error=true") // 로그인 실패 시 리다이렉트
	            	    .permitAll())
	            .logout(logout -> logout.logoutUrl("/logout") // 로그아웃 URL 설정
	                .logoutSuccessUrl("/main") // 로그아웃 성공 시 이동할 URL
	                .invalidateHttpSession(true) // 세션 무효화
	                .deleteCookies("JSESSIONID") // 로그아웃 시 세션 쿠키 삭제
	                .permitAll()) // 로그아웃은 누구나 접근 가능
	            .securityContext(securityContext -> securityContext.requireExplicitSave(false)) // SecurityContext 자동 저장
	            .sessionManagement(session -> session
	            		.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)  // IF_REQUIRED 대신 ALWAYS 사용
	            	    .maximumSessions(1)
	            	    .expiredUrl("/login?expired"))	            	    
	            .userDetailsService(userDetailsService) // 커스텀 UserDetailsService 사용
	            .formLogin() // 기본 폼 로그인 활성화
	            .disable(); // 로그인 폼의 기본 설정 사용 (커스터마이징 가능)

	        return http.build();
	    }
	}