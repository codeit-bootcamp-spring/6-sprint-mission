package com.sprint.mission.discodeit.dto.MessageDto;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest(
        UUID channelId,
        UUID authorId,
        String content,
        List<BinaryContentDto> attachments
) {
}