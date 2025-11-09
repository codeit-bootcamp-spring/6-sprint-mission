package com.sprint.mission.discodeit.dto.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MessageCreateRequest (
        @NotNull(message = "채널 정보는 필수입니다")
        UUID channelId,

        @NotNull(message = "유저 정보는 필수입니다")
        UUID authorId,

        @NotBlank(message = "메시지는 필수입니다")
        String content
){
}
