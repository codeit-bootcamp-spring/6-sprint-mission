package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateStorageNotificationRequest(
    String requestId,
    UUID binaryContentId,
    String errorType,
    String errorMessage

) {

}
