package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "메시지 생성 요청 DTO")
public record MessageCreateRequestDto(

        @NotNull(message = "사용자 ID는 필수 입력값입니다.")
        UUID authorId,

        @NotNull(message = "채널 ID는 필수 입력값입니다.")
        UUID channelId,

        @NotBlank(message = "메시지 내용을 입력해 주세요.")
        String content
) {}
