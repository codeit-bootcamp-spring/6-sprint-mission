package com.sprint.mission.discodeit.dto.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestDTO {

  @Builder
  public record UserCreateRequest(
      @NotBlank(message = "계정명을 입력하세요.")
      String username,
      @NotBlank(message = "이메일을 입력하세요.")
      @Email
      String email,
      @NotBlank(message = "비밀번호를 입력하세요.")
      @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$"
          , message = "비밀번호는 최소 8자, 하나 이상의 대문자, 소문자, 숫자 및 특수 문자를 포함해야 합니다.")
      String password,
      String description) {

  }

  @Builder
  public record UserUpdateRequest(
      @JsonProperty("newUsername")
      @NotBlank(message = "계정명을 입력하세요.")
      String username,
      @JsonProperty("newEmail")
      @NotBlank(message = "이메일을 입력하세요.")
      @Email
      String email,
      @NotBlank(message = "기존 비밀번호를 입력하세요.")
      String currentPassword,
      @NotBlank(message = "새 비밀번호를 입력하세요.")
      @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$"
          , message = "비밀번호는 최소 8자, 하나 이상의 대문자, 소문자, 숫자 및 특수 문자를 포함해야 합니다.")
      String newPassword
  ) {

  }

  @Builder
  public record UserStatusUpdateRequest(Instant newLastActiveAt) {

  }

  @Builder
  public record UserRoleUpdateRequest(
      @NotBlank(message = "사용자 ID를 입력하세요.")
      UUID userId,
      @NotNull(message = "새로운 역할을 입력하세요.")
      Role newRole
  ) {

  }

}
