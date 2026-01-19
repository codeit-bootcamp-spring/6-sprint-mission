package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SseMessageEntity {

  private UUID eventId;
  private String eventName;
  private Object data;
  private Set<UUID> receiverIdSet = new HashSet<>();
  private Boolean isForAll = false;

  @Builder(access = AccessLevel.PROTECTED)
  public SseMessageEntity(UUID eventId, String eventName, Object data,
      Set<UUID> receiverIdSet, Boolean isForAll) {
    this.eventId = eventId;
    this.eventName = eventName;
    this.data = data;
    this.receiverIdSet = receiverIdSet;
    this.isForAll = isForAll;
  }

  public static SseMessageEntity of(UUID id, String eventName, Object data,
      Set<UUID> receiverIdSet, Boolean isForAll) {
    return SseMessageEntity.builder()
        .eventId(id)
        .eventName(eventName)
        .data(data)
        .receiverIdSet(receiverIdSet)
        .isForAll(isForAll)
        .build();
  }

  public static SseMessageEntity createForAll(String eventName, Object data) {
    return SseMessageEntity.builder()
        .eventId(UUID.randomUUID())
        .eventName(eventName)
        .data(data)
        .isForAll(true)
        .build();
  }

  public static SseMessageEntity createForPrivate(String eventName, Object data,
      Set<UUID> receiverIdSet) {
    return SseMessageEntity.builder()
        .eventId(UUID.randomUUID())
        .eventName(eventName)
        .data(data)
        .receiverIdSet(receiverIdSet)
        .isForAll(false)
        .build();
  }

}
