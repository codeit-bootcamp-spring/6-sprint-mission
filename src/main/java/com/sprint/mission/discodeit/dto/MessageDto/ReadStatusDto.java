package com.sprint.mission.discodeit.dto.MessageDto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(
        UUID userId,
        boolean isRead,
        UUID channelId,
        Instant updatedAt
) {
}
