package com.sprint.mission.discodeit.dto.MessageDto;

import java.util.UUID;

public record BinaryContentDto(
        UUID userId,
        String contentType,
        String filename,
        byte[] data
) {
}