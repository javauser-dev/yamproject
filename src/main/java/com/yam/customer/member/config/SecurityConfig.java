package com.yam.customer.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; //추가

import com.yam.customer.member.service.MemberDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(@Lazy MemberDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((authz) -> authz
                        // 1. BAN_CUSTOMER는 /admin/info/banCustomer 만 허용 (가장 먼저)
                        .requestMatchers(new AntPathRequestMatcher("/admin/info/banCustomer")).hasAuthority("BAN_CUSTOMER")

                        // 2. 인증된 사용자는 /customer/reserve/** 허용
                        .requestMatchers(new AntPathRequestMatcher("/customer/reserve/**")).authenticated()

                        // 3. 관리자는 /admin/info/** 허용
                        .requestMatchers(new AntPathRequestMatcher("/admin/info/**")).hasAuthority("ROLE_ADMIN")

                        // 4. 나머지 요청은 모두 허용
                        .anyRequest().permitAll()
                )
                .formLogin((formLogin) -> formLogin
                        .loginPage("/customer/login")
                        .usernameParameter("customerId")
                        .passwordParameter("customerPassword")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/customer/login?error=true")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/customer/logout")
                        .logoutSuccessUrl("/customer/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }
}