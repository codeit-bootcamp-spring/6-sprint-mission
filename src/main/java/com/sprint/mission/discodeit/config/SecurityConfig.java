package com.sprint.mission.discodeit.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("message", "인증되지 않은 사용자")));
                        }))
                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("messge","권한이 없는 사용자")));
                        }))
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**",
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers(
                                "/",
                                "/api/auth/csrf-token",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/users"
                                ).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .formLogin(loing -> loing
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID","XSRF-TOKEN")
                        .logoutSuccessHandler(logoutSuccessHandler)
                )
                .build();
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()) {
                User user = new User(
                        "admin",
                        "admin@admin.com",
                        passwordEncoder.encode("admin"),
                        null,
                        Role.ADMIN
                        );
                userRepository.save(user);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        return RoleHierarchyImpl.fromHierarchy(
        """
            ROLE_ADMIN > ROLE_CHANNEL_MANAGER
            ROLE_CHANNEL_MANAGER > ROLE_USER
        """
        );
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy){
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}
