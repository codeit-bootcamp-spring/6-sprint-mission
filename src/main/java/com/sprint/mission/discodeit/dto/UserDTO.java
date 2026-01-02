package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.BinaryContentDTO.BinaryContentCreateCommand;
import com.sprint.mission.discodeit.entity.enums.Role;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserDTO {

  //login request DTO
  public record LoginCommand(String username, String password) {

  }

  public static LoginCommand toLoginCommand(String username, String password) {
    return new LoginCommand(username, password);
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class User {

    private UUID id;
    private String username;
    private String email;
    private String password;
    private BinaryContentDTO.BinaryContent profileId;
    private Role role;
    private Boolean isOnline;

    public void updateStatus(boolean online) {
      this.isOnline = online;
    }

  }

  @Builder
  public record CreateUserCommand(
      String username,
      String email,
      String password,
      String description,
      BinaryContentCreateCommand profileImage) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public boolean isEmailValid(String email) {
      return EMAIL_PATTERN.matcher(email).matches();
    }

    public CreateUserCommand {

      if (!isEmailValid(email) || username.isBlank()) {
        throw new IllegalArgumentException("Invalid user data.");
      }

    }

  }

  //find user을 위한 DTO
  @Builder
  public record FindUserResult(
      UUID id,
      String nickname,
      String email,
      String description,
      UUID profileImageId,
      Role role,
      boolean isOnline,
      Long createdAt,
      Long updatedAt
  ) {

  }

  //user update를 위한 Request DTO
  @Builder
  public record UpdateUserCommand(
      UUID id,
      String username,
      String email,
      String currentPassword,
      String newPassword,
      String description,
      boolean isProfileImageUpdated,
      BinaryContentCreateCommand profileImage
  ) {

    public boolean isEmailValid(String email) {
      return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public boolean isPasswordValid(String password) {
      //길이는 8자리 이상, 영어 대소문자, 숫자, 특수문자 포함
      return password.matches(
          "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$");
    }

    public UpdateUserCommand {

      if (!isPasswordValid(newPassword) || !isEmailValid(email) || username.isBlank()) {
        throw new IllegalArgumentException("Invalid user data.");
      }

    }

  }

  @Builder
  public record UpdateUserRoleCommand(
      UUID userId,
      Role newRole
  ) {

  }

}
