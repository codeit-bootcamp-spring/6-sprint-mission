package com.sprint.mission.discodeit.event.event;

import com.sprint.mission.discodeit.dto.NotificationDTO;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationEvent {

  private UUID receiverId;
  private NotificationDTO data;

  @Builder(access = AccessLevel.PROTECTED)
  public NotificationEvent(UUID receiverId, NotificationDTO data) {
    this.receiverId = receiverId;
    this.data = data;
  }

  public static NotificationEvent of(UUID receiverId, NotificationDTO data) {
    return NotificationEvent.builder()
        .receiverId(receiverId)
        .data(data)
        .build();
  }

}
