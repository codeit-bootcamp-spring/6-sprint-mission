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
public class MessageCreatedEvent {

  private UUID channelId;
  private UUID messageId;

  @Builder(access = AccessLevel.PROTECTED)
  private MessageCreatedEvent(UUID channelId, UUID messageId) {
    this.channelId = channelId;
    this.messageId = messageId;
  }

  public static MessageCreatedEvent of(UUID channelId, UUID messageId) {
    return MessageCreatedEvent.builder()
        .channelId(channelId)
        .messageId(messageId)
        .build();
  }

}
