package com.sprint.mission.discodeit.event;

import java.util.UUID;

/**
 * 메타데이터가 DB애 잘 저장되었음을 알리는 이벤트
 */
public record BinaryContentCreatedEvent (
        UUID id,
        byte[] bytes
)  {

}
