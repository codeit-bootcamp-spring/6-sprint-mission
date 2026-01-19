package com.sprint.mission.discodeit.event.event;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BinaryContentUpdatedEvent {

  private UUID receiverId;
  private BinaryContentDTO.BinaryContent data;

  @Builder(access = AccessLevel.PROTECTED)
  public BinaryContentUpdatedEvent(UUID receiverId,
      BinaryContentDTO.BinaryContent data) {
    this.receiverId = receiverId;
    this.data = data;
  }

  public static BinaryContentUpdatedEvent of(UUID receiverId,
      BinaryContentDTO.BinaryContent data) {
    return BinaryContentUpdatedEvent.builder()
        .receiverId(receiverId)
        .data(data)
        .build();
  }

}
