package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentPutFailEvent(
        String requestId,
        UUID binaryContentId,
        String errorMessage
) {
}
