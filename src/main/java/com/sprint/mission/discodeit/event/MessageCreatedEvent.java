package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageCreatedEvent(
    UUID channelId,
    String channelName,
    Message message,
    UUID authorId,
    String authorName
) {

}
