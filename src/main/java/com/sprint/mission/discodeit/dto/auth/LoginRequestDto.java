package com.sprint.mission.discodeit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequestDto (
/*
        @NotBlank(message = "닉네임을 입력해 주세요.")
        String username,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        String password*/
){
}
