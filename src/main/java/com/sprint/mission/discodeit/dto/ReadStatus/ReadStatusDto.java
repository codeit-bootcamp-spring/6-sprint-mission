package com.sprint.mission.discodeit.dto.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto (
        UUID id,
        UUID userId,
        UUID channelId,
        boolean notification_enabled,
        Instant lastReadAt
){
}
