package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 회원가입 요청 DTO")
public record UserCreateRequestDto(

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 50, message = "이메일은 50자까지 입력 가능합니다.")
        @NotBlank(message = "이메일을 입력해 주세요.")
        String email,

        @Size(max = 20, message = "닉네임은 20자까지 입력 가능합니다.")
        @NotBlank(message = "닉네임을 입력해 주세요.")
        String username,

        @Size(min = 8, max = 20, message = "비밀번호는 8자~20자 사이여야 합니다.")
        @NotBlank(message = "비밀번호를 입력해 주세요.")
        String password
) {}
