package com.sprint.mission.discodeit.event.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class MessageCreatedEvent {
    private final String senderUsername;
    private final String content;
    private final UUID channelId;
}
