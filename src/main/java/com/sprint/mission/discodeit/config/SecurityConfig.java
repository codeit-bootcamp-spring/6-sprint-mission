package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.UserDTO.UpdateUserRoleCommand;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.security.handler.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationSuccessHandler loginSuccessHandler;
  private final AuthenticationFailureHandler loginFailureHandler;
  private final LogoutSuccessHandler logoutSuccessHandler;
  private final UserService userService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(logoutSuccessHandler)
        )
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().permitAll()
        )
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

  public CommandLineRunner initAdminAccount() {
    return args -> {

      if (!userService.existUserByUsername("admin")) {
        UserDTO.CreateUserCommand adminUser = new UserDTO.CreateUserCommand(
            "admin",
            "admin@admin.com",
            "Admin@1234",
            "관리자",
            null
        );

        userService.updateUserRole(UpdateUserRoleCommand.builder()
                .userId(userService.createUser(adminUser).getId())
                .newRole(Role.ADMIN)
            .build());

      }
    };
  }
}
