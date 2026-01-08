package com.sprint.mission.discodeit.dto.BinaryContent;

import java.util.UUID;

public record BinaryContentCreatedEvent(
        UUID id,
        byte[] content
) {
}
