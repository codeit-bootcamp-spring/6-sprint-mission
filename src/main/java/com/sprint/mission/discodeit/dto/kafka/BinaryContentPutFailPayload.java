package com.sprint.mission.discodeit.dto.kafka;

import java.util.UUID;

public record BinaryContentPutFailPayload(
        String requestId,
        UUID binaryContentId,
        String errorMessage
){
}
