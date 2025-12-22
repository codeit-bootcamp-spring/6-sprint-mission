package com.sprint.mission.discodeit.security.runner;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.UserDTO.UpdateUserRoleCommand;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminRunner {

  private final UserService userService;

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
