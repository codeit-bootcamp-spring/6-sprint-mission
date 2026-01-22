package com.sprint.mission.discodeit.event.sse;

import java.util.Collection;
import java.util.UUID;
import lombok.Getter;

@Getter
public class SseDispatchEvent {

  private final Collection<UUID> receiverIds;
  private final String eventName;
  private final Object data;

  public SseDispatchEvent(Collection<UUID> receiverIds, String eventName, Object data) {
    this.receiverIds = receiverIds;
    this.eventName = eventName;
    this.data = data;
  }
}
