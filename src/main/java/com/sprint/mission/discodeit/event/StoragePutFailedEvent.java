package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Builder;

@Builder
public record StoragePutFailedEvent(
    String requestId,
    UUID binaryContentId,
    String errorType,
    String errorMessage
) {

}
