package com.sprint.mission.discodeit.dto.MessageDto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateDto(
        UUID userId,
        UUID messageId,
        Instant lastReadAt
) {
}
