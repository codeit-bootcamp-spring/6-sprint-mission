package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.filter.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.handler.SpaCsrfTokenRequestHandler;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationSuccessHandler loginSuccessHandler;
  private final AuthenticationFailureHandler loginFailureHandler;
  private final LogoutSuccessHandler logoutSuccessHandler;
  private final LogoutHandler logoutHandler;
  private final UserDetailsService userDetailsService;
  private final DataSource dataSource;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Value("${security.key.remember-me}")
  private String rememberMeKey;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
            .permitAll()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            /*.sessionConcurrency(concurrency -> {
              concurrency
                  .maximumSessions(1)
                  .maxSessionsPreventsLogin(false)
                  .expiredUrl("/api/auth/login?expired")
                  .sessionRegistry(sessionRegistry());
            })*/
        )
        /*.rememberMe(remember -> remember
            .key(rememberMeKey)
            .rememberMeParameter("remember-me")
            .userDetailsService(userDetailsService)
            .tokenRepository(tokenRepository())
            .authenticationSuccessHandler(loginSuccessHandler)
            .tokenValiditySeconds(60 * 60 * 24 * 30))*/
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(logoutHandler)
            .logoutSuccessHandler(logoutSuccessHandler)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(
                "/",
                "/index.html"
            ).permitAll()
            .requestMatchers("/index.html",
                "/assets/**",
                "favicon.ico",
                "/api/auth/login",
                "/api/auth/csrf-token",
                "/api/auth/logout",
                "/actuator/health",
                "/actuator/info",
                "/api/auth/refresh")
            .permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .csrf(csrf ->
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  /*@Bean
  public PersistentTokenRepository tokenRepository() {
    JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
    tokenRepository.setDataSource(dataSource);
    return tokenRepository;
  }*/

}
