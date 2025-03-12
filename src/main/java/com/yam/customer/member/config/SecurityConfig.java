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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; //추가

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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf((csrf) -> csrf.disable()) //테스트를 위해 csrf 보호 비활성화. 프로덕션에서는 사용 X
            .authorizeHttpRequests((authz) -> authz
                 // 변경된 부분 (AntPathRequestMatcher 사용)
                .requestMatchers(new AntPathRequestMatcher("/customer/reserve/**")).authenticated()
                //.requestMatchers(new AntPathRequestMatcher("/admin/info/banCustomer")).hasRole("BAN_CUSTOMER") // BAN_CUSTOMER 롤만 접근 가능
                .requestMatchers(new AntPathRequestMatcher("/admin/info/banCustomer")).hasAuthority("BAN_CUSTOMER") // 권한 확인
                .anyRequest().permitAll() // 나머지 요청은 모두 허용
            )
            .formLogin((formLogin) -> formLogin
                .loginPage("/customer/login")  // 로그인 페이지
                .usernameParameter("customerId")  //아이디 파라미터
                .passwordParameter("customerPassword") //비밀번호 파라미터
                .defaultSuccessUrl("/customer/myPage")  //로그인 성공시 이동할 페이지
                .permitAll()
             )
            .logout((logout) -> logout
                .logoutUrl("/customer/logout")     // 로그아웃 URL
                .logoutSuccessUrl("/customer/login") // 로그아웃 성공 후 리다이렉트 URL
                .invalidateHttpSession(true)       // 세션 무효화
                .deleteCookies("JSESSIONID")        // 쿠키 삭제 (선택 사항)
                .permitAll()
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }
}