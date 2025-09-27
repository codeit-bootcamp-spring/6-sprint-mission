package com.sprint.mission.discodeit.dto.MessageDto;

import java.util.UUID;

public record UpdateMessageRequest(
        UUID messageId,
        String newContent
) {
}
