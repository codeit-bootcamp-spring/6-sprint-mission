package com.sprint.mission.discodeit.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final ObjectMapper objectMapper;


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/assets/**",
                "/favicon.ico",
                "/index.html",
                "/api/auth/csrf-token",
                "/api/auth/login",
                "/api/auth/logout",
                "/actuator/health").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers("/api/auth/me").authenticated()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
                  response.setStatus(HttpStatus.UNAUTHORIZED.value());
                  response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                  objectMapper.writeValue(response.getWriter(), Map.of("message", "로그인이 필요한 서비스입니다."));
                }
            )
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                  response.setStatus(HttpStatus.FORBIDDEN.value());
                  response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                  objectMapper.writeValue(response.getWriter(), Map.of("message", "접근 권한이 없습니다."));
                }
            )
        )
        .csrf(csrf -> csrf
            // Double Submit Cookie Pattern
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
        );
    return http.build();
  }

  // 역할 계층 정의
  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
        "ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n"
            + "ROLE_CHANNEL_MANAGER > ROLE_USER");
  }

  // 메서드 보안에 역할 계층 적용
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {

    return args -> {
      if (userRepository.findByUsername("admin").isEmpty()) {
        userRepository.save(
            User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("testtest"))
                .role(Role.ADMIN)
                .build()
        );
      }
    };
  }
}
