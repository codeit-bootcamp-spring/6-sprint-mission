package com.sprint.mission.discodeit.sse.dto;

import java.util.List;
import java.util.UUID;

public record SseMulticastMessageEvent(
        List<UUID> receiverIds,
        String name,
        Object date
) {
}
