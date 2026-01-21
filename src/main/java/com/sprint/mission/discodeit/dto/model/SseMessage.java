package com.sprint.mission.discodeit.dto.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record SseMessage(
    UUID id,
    UUID receiverId,
    String eventName,
    Object data,
    LocalDateTime createdAt
) {

}
