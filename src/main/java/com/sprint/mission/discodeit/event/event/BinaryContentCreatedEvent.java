package com.sprint.mission.discodeit.event.event;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access =  AccessLevel.PROTECTED)
@Getter
public class BinaryContentCreatedEvent {

  private UUID id;
  private byte[] binaryContent;

  public static BinaryContentCreatedEvent of(UUID id, byte[] binaryContent) {
    return BinaryContentCreatedEvent.builder()
        .id(id)
        .binaryContent(binaryContent)
        .build();
  }

}
