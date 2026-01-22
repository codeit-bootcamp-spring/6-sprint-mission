package com.sprint.mission.discodeit.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private static final int MAX_SIZE = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public SseMessage save(SseMessage message) {
    messages.put(message.id(), message);
    eventIdQueue.addLast(message.id());
    trimIfNeeded();
    return message;
  }

  public List<SseMessage> findAfter(UUID receiverId, UUID lastEventId) {
    if (lastEventId == null) {
      return findAllByReceiver(receiverId);
    }

    boolean found = false;
    List<SseMessage> results = new ArrayList<>();
    for (UUID eventId : eventIdQueue) {
      if (!found) {
        if (eventId.equals(lastEventId)) {
          found = true;
        }
        continue;
      }
      SseMessage message = messages.get(eventId);
      if (message != null && isTarget(receiverId, message)) {
        results.add(message);
      }
    }

    return found ? results : findAllByReceiver(receiverId);
  }

  private List<SseMessage> findAllByReceiver(UUID receiverId) {
    List<SseMessage> results = new ArrayList<>();
    for (UUID eventId : eventIdQueue) {
      SseMessage message = messages.get(eventId);
      if (message != null && isTarget(receiverId, message)) {
        results.add(message);
      }
    }
    return results;
  }

  private boolean isTarget(UUID receiverId, SseMessage message) {
    return message.receiverId() == null || message.receiverId().equals(receiverId);
  }

  private void trimIfNeeded() {
    while (eventIdQueue.size() > MAX_SIZE) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) {
        messages.remove(oldest);
      }
    }
  }
}
