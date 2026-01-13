package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto (
        UUID id,
        String content,
        String title,
        UUID receiverId,
        Instant createdAt
) {
}
