package com.sprint.mission.discodeit.security.runner;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.UserDTO.UpdateUserRoleCommand;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminRunner implements ApplicationRunner {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public void run(ApplicationArguments args) throws Exception {

    Authentication auth =
        new UsernamePasswordAuthenticationToken(
            "system",
            null,
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);

    try {
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
    } finally {
      SecurityContextHolder.clearContext();
    }


  }
}
