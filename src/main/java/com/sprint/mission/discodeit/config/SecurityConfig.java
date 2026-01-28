package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.handler.*;
import com.sprint.mission.discodeit.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final LoginFailureHandler loginFailureHandler;
    private final HttpStatusReturningLogoutSuccessHandler httpStatusReturningLogoutSuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtLogoutHandler jwtLogoutHandler;
    private final SpaCsrfTokenRequestHandler spaCsrfTokenRequestHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(spaCsrfTokenRequestHandler))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                        "/index.html",
                                        "/assets/**",
                                        "favicon.ico",
                                        "/api/auth/login",
                                        "/api/auth/csrf-token",
                                        "/api/auth/logout",
                                        "/actuator/health",
                                        "/actuator/info",
                                        "/api/auth/refresh",
                                        "/ws/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                                .requestMatchers("/actuator/**").hasRole("ADMIN")
                                .requestMatchers(
                                        "/index.html",
                                        "/assets/**",
                                        "/favicon.ico",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html").permitAll()
                                .anyRequest().authenticated()
                ).formLogin(x -> x
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(jwtLoginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .permitAll()
                ).logout(x -> x
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(httpStatusReturningLogoutSuccessHandler)
                        .permitAll()
                ).exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN))
                        .accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository,
                                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.existsByRole(Role.ROLE_ADMIN)) {
                return;
            }

            User user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .email("admin@test.com")
                    .profile(null)
                    .build();

            user.updateRole(Role.ROLE_ADMIN);
            userRepository.save(user);
        };
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");
        return roleHierarchy;
    }

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
}
