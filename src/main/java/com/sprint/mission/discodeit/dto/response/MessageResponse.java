package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID channelId,
        UUID senderId,
        String content,
        Instant createdAt,
        List<BinaryContentResponse> attachments
) {

    public static MessageResponse from(Message message) {

        List<BinaryContentResponse> attachmentResponses = message.getAttachments() != null
                ? BinaryContentResponse.fromList(message.getAttachments())
                : Collections.emptyList();

        return new MessageResponse(
                message.getId(),
                message.getChannel().getId(),
                message.getAuthor().getId(),
                message.getContent(),
                message.getCreatedAt(),
                attachmentResponses
        );
    }
}