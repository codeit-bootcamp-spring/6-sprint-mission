package com.sprint.mission.discodeit.event;

import java.util.UUID;

// 배열 필드있으면 equals, hashcode 문제 발생할 수 있음?
public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] file
) {

}
