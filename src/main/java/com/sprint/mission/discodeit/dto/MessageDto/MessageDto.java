package com.sprint.mission.discodeit.dto.MessageDto;

import java.util.UUID;

public record MessageDto(
        UUID id,
        String content,
        UUID channelId,
        UUID authorId
) {
}
