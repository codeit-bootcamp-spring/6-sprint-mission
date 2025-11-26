package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "비공개 채널 생성 요청 DTO")
public record PrivateChannelCreateRequestDto (

        @NotNull(message = "채널 구성원 목록은 필수 입력값입니다.")
        @NotEmpty(message = "채널 구성원 목록은 비어있을 수 없습니다.")
        @Size(min = 2, message = "비공개 채널에는 최소 2명의 참여자가 필요합니다")
        List<UUID> participantIds
){}
