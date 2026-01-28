package com.sprint.mission.discodeit.repository.sse;

import com.sprint.mission.discodeit.dto.model.SseMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  // 순서 확인 큐
  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  // 메시지 저장 맵
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public void save(SseMessage sseMessage) {
    messages.put(sseMessage.id(), sseMessage);
    eventIdQueue.addLast(sseMessage.id());
  }

  public SseMessage findById(UUID id) {
    return messages.get(id);
  }

  public List<SseMessage> findAllByReceiverId(UUID receiverId) {
    return eventIdQueue.stream()
        .map(messages::get)
        .filter(Objects::nonNull)
        .filter(msg -> msg.receiverId().equals(receiverId))
        .toList();
  }

  public void deleteUpTo(UUID lastEventId) {

    if (!messages.containsKey(lastEventId)) {
      return;
    }
    UUID eventId;
    while ((eventId = eventIdQueue.pollFirst()) != null) {
      messages.remove(eventId);
      if (eventId.equals(lastEventId)) {
        break;
      }
    }
  }

  public void deleteOlderThan(LocalDateTime threshold) {
    UUID eventId;
    while ((eventId = eventIdQueue.peekFirst()) != null) {
      SseMessage msg = messages.get(eventId);
      if (msg != null && msg.createdAt().isBefore(threshold)) {
        eventIdQueue.pollFirst();
        messages.remove(eventId);
      } else {
        break;
      }
    }
  }
}
