package com.sprint.mission.discodeit.event.event;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BinaryContentCreatedEvent {
    private final UUID binaryContentId;
    private final byte[] bytes;
    private final BinaryContentStorage  binaryContentStorage;
}
