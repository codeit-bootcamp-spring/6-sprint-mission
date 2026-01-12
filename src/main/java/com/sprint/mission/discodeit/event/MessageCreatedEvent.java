package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageCreatedEvent(
    UUID channelId,
    UUID messageId,
    UUID authorId,
    String content
) {

}
