package com.sprint.mission.discodeit.dto.MessageDto;

import java.util.UUID;

public record ReadStatusCreateDto(
        UUID userId,
        UUID channelId
) {
}
