package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

@Schema(description = "읽음 상태 수정 요청 DTO")
public record ReadStatusUpdateRequestDto (

        @PastOrPresent(message = "마지막 읽은 시간은 현재 또는 과거 시간이어야 합니다")
        Instant newLastReadAt,

        boolean newNotificationEnabled
){}
