package com.sprint.mission.discodeit.sse;

import java.time.Instant;
import java.util.UUID;

public record SseMessage(
    UUID id,
    String eventName,
    Object data,
    UUID receiverId,
    Instant createdAt
) {

}
