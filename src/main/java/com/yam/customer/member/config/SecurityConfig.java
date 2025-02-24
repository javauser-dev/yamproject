package com.yam.customer.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService; // 주입

    // @Lazy 추가
    public SecurityConfig(@Lazy UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests((authz) -> authz
//                .requestMatchers("/", "/customer/signup", "/customer/signup-success", "/js/**", "/css/**", "/customer/checkId", "/customer/checkNickname").permitAll()
//                .anyRequest().authenticated()
//            )
	    	http.csrf((csrf) -> csrf.disable())
	 	    .authorizeHttpRequests((authz) -> authz
	 	        .anyRequest().permitAll()
	 	    )
            .formLogin((formLogin) -> formLogin
                    .loginPage("/customer/login")
                     .usernameParameter("customerId")
                     .passwordParameter("customerPassword")
                     .defaultSuccessUrl("/")
                    .permitAll()
            )
            .logout((logout)-> logout.permitAll())
            .userDetailsService(userDetailsService);
        return http.build();
    }
}