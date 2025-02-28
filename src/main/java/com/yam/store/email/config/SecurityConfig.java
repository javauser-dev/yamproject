package com.yam.store.email.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.yam.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {
/*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
    	http
            .csrf().disable()
//            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/store/login", "/store/signup").permitAll() // 로그인 & 회원가입 허용
                    .anyRequest().authenticated())
       
        .formLogin(form -> form
            .loginPage("/store/login") // 커스텀 로그인 페이지 설정
            .defaultSuccessUrl("/store/dashboard", true) // 로그인 성공 후 이동할 페이지
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/store/login")
            .permitAll()
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 // ✅ 로그인 성공 시 무한 리디렉션 방지
    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {
        return new AuthenticationSuccessHandler() {
			
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                // 사용자가 이미 인증된 경우, 로그인 페이지 접근 시 대시보드로 리디렉션
                if (authentication != null && authentication.isAuthenticated()) {
                    response.sendRedirect("/store/dashboard");
                } else {
                    response.sendRedirect("/store/login");
                }
            }
        };
    }
    */
	
	private final CustomUserDetailsService customUserDetailsService;
	 private final PasswordEncoder passwordEncoder;

	    public SecurityConfig(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
	        this.customUserDetailsService = customUserDetailsService;
	        this.passwordEncoder = passwordEncoder;
	    }
	
	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (필요하면 활성화 가능)
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/login", "/doLogin").permitAll()
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                .loginPage("/login")
	                .loginProcessingUrl("/doLogin") // 커스텀 로그인 경로
	                .usernameParameter("email") // 기존 "username" 대신 "email" 사용
	                .passwordParameter("password")
	                .defaultSuccessUrl("/")
	                .failureUrl("/login?error=true")
	                .permitAll()
	            )
	            .logout(logout -> logout
	                .logoutUrl("/logout")
	                .logoutSuccessUrl("/login?logout=true")
	                .invalidateHttpSession(true)
	                .deleteCookies("JSESSIONID")
	                .permitAll()
	            );

	        return http.build();
	    }
	

	    @Bean
	    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
	        return http.getSharedObject(AuthenticationManagerBuilder.class)
	                .userDetailsService(customUserDetailsService)
	                .passwordEncoder(passwordEncoder())
	                .and()
	                .build();
	    }
	    @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	        authProvider.setUserDetailsService(customUserDetailsService); // 여기서 email 기반으로 인증
	        authProvider.setPasswordEncoder(passwordEncoder());
	        return authProvider;
	    }
	  @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	
}