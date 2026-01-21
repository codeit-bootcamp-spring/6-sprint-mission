package com.sprint.mission.discodeit.dto.kafka;

import java.util.UUID;

public record MessageCreatedPayload(
        UUID channelId,
        String channelName,
        UUID authorId,
        String authorUsername,
        String content
) {
}
