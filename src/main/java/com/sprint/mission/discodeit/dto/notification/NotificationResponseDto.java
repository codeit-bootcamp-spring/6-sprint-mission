package com.sprint.mission.discodeit.dto.notification;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record NotificationResponseDto (
        UUID id,
        UUID receiverId,
        String title,
        String content,
        Instant createdAt
) {

}
