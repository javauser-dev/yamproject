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

    private final UserDetailsService userDetailsService;

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
        http
            .csrf((csrf) -> csrf.disable()) //테스트를 위해 csrf 보호 비활성화. 프로덕션에서는 사용 X
            .authorizeHttpRequests((authz) -> authz
                .anyRequest().permitAll() // 모든 URL 허용 (*테스트용*)
            )
            .formLogin((formLogin) -> formLogin
                    .loginPage("/customer/login")
                     .usernameParameter("customerId")
                     .passwordParameter("customerPassword")
                     .defaultSuccessUrl("/customer/myPage")
                    .permitAll()
             )
            .logout((logout) -> logout //이부분 확인
                .logoutUrl("/customer/logout")     // 로그아웃 URL (기본값은 /logout, POST)
                .logoutSuccessUrl("/customer/login") // 로그아웃 성공 후 리다이렉트 URL
                .invalidateHttpSession(true)       // 세션 무효화
                .deleteCookies("JSESSIONID")        // 쿠키 삭제 (선택 사항)
                .permitAll()
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }
}