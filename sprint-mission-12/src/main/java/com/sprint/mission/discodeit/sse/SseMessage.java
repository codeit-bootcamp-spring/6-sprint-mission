package com.sprint.mission.discodeit.sse;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

public record SseMessage(
    String eventName,
    Object data,
    Instant createdAt,
    Collection<UUID> receiverIds
) {}
