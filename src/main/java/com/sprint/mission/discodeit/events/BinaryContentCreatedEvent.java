package com.sprint.mission.discodeit.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class BinaryContentCreatedEvent {
    private final UUID userId;
    private final UUID binaryContentId;
    private final byte[] bytes;
}
