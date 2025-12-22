package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 공격 방어 설정
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestAttributeHandler())
                )

                // HTTP 기본 인증 사용 여부
                .httpBasic(basic -> basic.disable())

                // 폼 로그인
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home") // TODO 성공 시 이동할 페이지
                        .permitAll()
                )

                // 인가 정책
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**", "/login", "/signup").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true) // 세션 무효화
                );

        return http.build();
    }

}
