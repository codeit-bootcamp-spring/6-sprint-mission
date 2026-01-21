package com.sprint.mission.discodeit.dto.Message;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MessageCreatedEvent(
        UUID userId,
        UUID channelId,
        String userName,
        String channelName,
        String content
) {
}
