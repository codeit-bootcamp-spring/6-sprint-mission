package com.sprint.mission.discodeit.event.event;

import com.sprint.mission.discodeit.dto.NotificationDTO;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationEvent {

  Instant name;
  NotificationDTO data;

  @Builder(access = AccessLevel.PROTECTED)
  public NotificationEvent(Instant name, NotificationDTO data) {
    this.name = name;
    this.data = data;
  }

  public static NotificationEvent of(Instant name, NotificationDTO data) {
    return NotificationEvent.builder()
        .name(name)
        .data(data)
        .build();
  }

}
