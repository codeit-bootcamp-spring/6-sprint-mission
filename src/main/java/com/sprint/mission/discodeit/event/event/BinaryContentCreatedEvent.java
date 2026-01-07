package com.sprint.mission.discodeit.event.event;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BinaryContentCreatedEvent {

  private UUID id;
  private byte[] binaryContent;

  @Builder(access = AccessLevel.PROTECTED)
  private BinaryContentCreatedEvent(UUID id, byte[] binaryContent) {
    this.id = id;
    this.binaryContent = binaryContent;
  }

  public static BinaryContentCreatedEvent of(UUID id, byte[] binaryContent) {
    return BinaryContentCreatedEvent.builder()
        .id(id)
        .binaryContent(binaryContent)
        .build();
  }

}
