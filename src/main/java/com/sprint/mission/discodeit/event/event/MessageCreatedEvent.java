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
public class MessageCreatedEvent {

  private UUID channelId;
  private UUID messageId;

  public static MessageCreatedEvent of(UUID channelId, UUID messageId) {
    return MessageCreatedEvent.builder()
        .channelId(channelId)
        .messageId(messageId)
        .build();
  }

}
