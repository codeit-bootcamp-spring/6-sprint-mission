package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
        UUID id,
        Boolean status,
        UUID userId,
        Instant updatedAt,
        boolean isOnline
) {
}
