package com.sprint.mission.discodeit.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * 메타데이터가 DB애 잘 저장되었음을 알리는 이벤트
 */
@Getter
@AllArgsConstructor
public class BinaryContentCreatedEvent {
    UUID id;
    byte[] bytes;
}
