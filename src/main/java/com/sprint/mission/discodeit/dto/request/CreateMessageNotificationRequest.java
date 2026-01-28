package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateMessageNotificationRequest(
    UUID channelId,
    String channelName,
    UUID messageId,
    UUID authorId,
    String authorName,
    String content
) {

}
