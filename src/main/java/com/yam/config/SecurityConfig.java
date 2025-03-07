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
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/api/signup", "/customer/signup", "/store/signup", "/store/register",
								"/customer/checkId", "/customer/checkNickname", "/customer/sendVerificationCode",
								"/customer/verifyCode", "/login/**", "/customer/signup-success")
						.permitAll().requestMatchers("/admin/**", "/dashboard/**").hasAuthority("ROLE_ADMIN")
						.requestMatchers("/store/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE")
						.requestMatchers("/customer/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CUSTOMER").anyRequest()
						.permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/main").invalidateHttpSession(true)
						.deleteCookies("JSESSIONID").permitAll())
				.securityContext(securityContext -> securityContext.requireExplicitSave(false))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.userDetailsService(userDetailsService).formLogin().disable();

		return http.build();
	}

}
