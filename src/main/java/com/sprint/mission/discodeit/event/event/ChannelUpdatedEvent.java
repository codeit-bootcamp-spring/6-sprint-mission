package com.sprint.mission.discodeit.event.event;

import com.sprint.mission.discodeit.dto.ChannelDTO;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChannelUpdatedEvent {

  private UUID receiverId;
  private ChannelNotificationType type;
  private ChannelDTO.Channel data;

  @Getter
  public enum ChannelNotificationType {
    CREATED("channels.created"),
    UPDATED("channels.updated"),
    DELETED("channels.deleted");

    private String eventName;

    private ChannelNotificationType(String eventName) {
      this.eventName = eventName;
    }

  }

  @Builder(access = AccessLevel.PROTECTED)
  public ChannelUpdatedEvent(UUID receiverId, ChannelNotificationType type, ChannelDTO.Channel data) {
    this.receiverId = receiverId;
    this.type = type;
    this.data = data;
  }

  public static ChannelUpdatedEvent of(UUID receiverId, ChannelNotificationType type,
      ChannelDTO.Channel data) {
    return ChannelUpdatedEvent.builder()
        .receiverId(receiverId)
        .type(type)
        .data(data)
        .build();
  }

}
