package com.sprint.mission.discodeit.dto.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserCreateRequest(
        @NotBlank(message = "사용자 이름은 필수 입니다.")
        String username,
        @NotBlank(message = "이메일은 필수 입니다.")
        String email,
        @NotBlank(message = "비빌번호은 필수 입니다.")
        String password
){}
