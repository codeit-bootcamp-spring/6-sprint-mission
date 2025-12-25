package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf -> csrf
                        // 쿠키 저장소 설정 HttpOnly를 false로 설정
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // SPA 전용 핸들러 등록
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/csrf-token").permitAll() // 토큰 발급 API 개방
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login") // 로그인 처리 URL 설정
                        .successHandler(loginSuccessHandler)   // 성공 핸들러 대체
                        .failureHandler(loginFailureHandler)   // 실패 핸들러 대체
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
