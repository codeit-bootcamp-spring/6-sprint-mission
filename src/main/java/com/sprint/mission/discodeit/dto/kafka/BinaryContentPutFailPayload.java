package com.sprint.mission.discodeit.dto.kafka;

import java.util.UUID;

public record BinaryContentPutFailPayload(
        UUID requestId,
        UUID binaryContentId,
        String errorMessage
){
}
