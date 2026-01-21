package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Getter;

@Getter
public class MessageCreatedEvent {

    private final UUID messageId;
    private final UUID channelId;
    private final String channelName;
    private final UUID authorId;
    private final String authorName;
    private final String content;

    public MessageCreatedEvent(UUID messageId, UUID channelId, String channelName, UUID authorId,
        String authorName, String content) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.channelName = channelName;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
    }

}
