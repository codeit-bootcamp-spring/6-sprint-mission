package com.sprint.mission.discodeit.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final ObjectMapper objectMapper;
  private final UserDetailsService userDetailsService;
  private final DataSource dataSource;

  @Value("${remember.me.key}")
  private String rememberMeKey;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
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
            .requestMatchers(
                "/actuator/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/**").hasRole("ADMIN")
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
        .sessionManagement(session -> session
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry)
                .expiredSessionStrategy(event -> {
                  HttpServletResponse response = event.getResponse();
                  response.setStatus(HttpStatus.UNAUTHORIZED.value());
                  response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                  objectMapper.writeValue(response.getWriter(), Map.of("message", "다른 곳에서 로그인되어 세션이 만료되었습니다."));
                })
            )
            .invalidSessionStrategy((request, response) -> {
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
              response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
              objectMapper.writeValue(response.getWriter(), Map.of("message", "세션이 유효하지 않습니다. 다시 로그인 해주세요."));
            })
        )
        .rememberMe(rememberMe -> rememberMe
            .key(rememberMeKey)
            // 10일 동안 유효
            .tokenValiditySeconds(60 * 60 * 24 * 10)
            .rememberMeParameter("remember-me")
            .userDetailsService(userDetailsService)
            // Persistent Token 방식 설정
            .tokenRepository(tokenRepository())
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
            .deleteCookies("remember-me", "JSESSIONID")
        );
    return http.build();
  }

  @Bean
  public PersistentTokenRepository tokenRepository() {
    JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
    tokenRepository.setDataSource(dataSource);
    return tokenRepository;
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
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
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
      if (userRepository.findByUsername("channelManager").isEmpty()) {
        userRepository.save(
            User.builder()
                .username("channelManager")
                .email("channelManager@gmail.com")
                .password(passwordEncoder.encode("testtest"))
                .role(Role.CHANNEL_MANAGER)
                .build()
        );
      }
    };
  }
}
