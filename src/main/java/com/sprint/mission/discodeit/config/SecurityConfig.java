package com.sprint.mission.discodeit.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final JwtLogoutHandler jwtLogoutHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
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
                "/api/auth/refresh",
                "/api/auth/login",
                "/api/auth/logout",
                "/actuator/**",
                "/ws/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/**").hasRole("ADMIN")
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
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  // 디버그용 필터 체인 출력
  @Bean
  public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {
    return args -> {
      int filterSize = filterChain.getFilters().size();
      List<String> filterNames = IntStream.range(0, filterSize)
          .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
              filterChain.getFilters().get(idx).getClass()))
          .toList();
      log.debug("Debug Filter Chain...\n{}", String.join(System.lineSeparator(), filterNames));
    };
  }

  @Bean
  public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
    return new SimpleAuthorityMapper();
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
