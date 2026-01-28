package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.SseMessageEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessageEntity> messages = new ConcurrentHashMap<>();

  public void save(SseMessageEntity sseMessageEntity) {

    UUID eventId = sseMessageEntity.getEventId();
    messages.put(eventId, sseMessageEntity);
    eventIdQueue.addLast(eventId);

  }

  public SseMessageEntity findByEventId(UUID eventId) {
    return messages.get(eventId);
  }

  public void deleteByEventId(UUID eventId) {
    messages.remove(eventId);
    eventIdQueue.remove(eventId);
  }

  public List<SseMessageEntity> findAllAfterEventIdAndReceiverId(UUID lastEventId, UUID receiverId) {

    List<SseMessageEntity> result = new ArrayList<>();
    boolean isLastEventExist = false;

    // Iterate through the queue in order to find events after lastEventId
    for (UUID eventId : eventIdQueue) {

      if (!isLastEventExist) {
        if (eventId.equals(lastEventId)) {
          isLastEventExist = true;
        }
        continue;
      }

      SseMessageEntity message = messages.get(eventId);

      if (message != null) {
        if (message.getIsForAll() ||
            (message.getReceiverIdSet() != null && message.getReceiverIdSet().contains(receiverId))) {
          result.add(message);
        }
      }

    }

    return result;
  }

}
