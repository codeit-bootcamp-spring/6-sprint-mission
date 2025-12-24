package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.evaluator.GlobalPermissionEvaluator;
import com.sprint.mission.discodeit.security.formLogin.DiscodeitAuthenticationFailureHandler;
import com.sprint.mission.discodeit.security.formLogin.DiscodeitAuthenticationSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.session.DiscodeitInvalidSessionStrategy;
import com.sprint.mission.discodeit.security.session.DiscodeitSessionExpiredStrategy;
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
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
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

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final ObjectMapper mapper;
    private final GlobalPermissionEvaluator permissionEvaluator;
    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;
    @Value("${discodeit.security.remember-me-secret}")
    private String rememberMeSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf ->
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/index.html", "/favicon.ico", "/assets/**", "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .rememberMe(rememberMeConfigurer -> rememberMeConfigurer
                        .key(rememberMeSecret)
                        .userDetailsService(userDetailsService)
                        .rememberMeParameter("remember-me")
                        .tokenRepository(persistentTokenRepository())
                        .tokenValiditySeconds(60 * 60 * 24 * 7) //영구 저장 방식에서는 필요 없음
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(new DiscodeitAuthenticationSuccessHandler(mapper))
                        .failureHandler(new DiscodeitAuthenticationFailureHandler(mapper))
                        .permitAll()
                )
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                )
                .sessionManagement(management -> management
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId)
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //Stateless, Always, Never
                        .invalidSessionStrategy(new DiscodeitInvalidSessionStrategy(mapper))
                        .sessionConcurrency(concurrencyControlConfigurer -> concurrencyControlConfigurer
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                                .sessionRegistry(sessionRegistry())
                                .expiredSessionStrategy(new DiscodeitSessionExpiredStrategy(mapper))
                        )

                )
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .accessDeniedHandler((request, response, e) -> {
                                    response.setStatus(HttpStatus.FORBIDDEN.value());
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                                    mapper.writeValue(response.getWriter(), Map.of(
                                            "code", "PERMISSION_DENIED",
                                            "message", "CSRF 토큰이 없거나 권한이 부족합니다."
                                    ));
                                }
                        )
                        .authenticationEntryPoint((request, response, authException) -> {
                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                                    mapper.writeValue(response.getWriter(), Map.of(
                                            "code", "AUTHENTICATION_REQUIRED",
                                            "message", "인증되지 않은 사용자 입니다."
                                    ));
                                }
                        )
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isPresent()) {
                return;
            }
            String encodedPassword = passwordEncoder.encode("admin");
            User user = new User("admin", "admin@discodeit.com", encodedPassword, null);
            user.update(Role.ADMIN);
            userRepository.save(user);
        };
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_ADMIN > ROLE_CHANNEL_MANAGER
                ROLE_CHANNEL_MANAGER > ROLE_USER
                """);
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
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
}
