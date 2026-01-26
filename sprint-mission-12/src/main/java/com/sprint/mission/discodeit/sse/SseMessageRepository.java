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

  // 순서 유지(오래된 것부터 앞)
  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();

  // id -> message
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  // 메모리 폭주 방지: 최근 N개만 유지 (필요에 맞게 조절)
  private static final int MAX_EVENTS = 5_000;

  public UUID save(SseMessage message) {
    UUID id = UUID.randomUUID();
    messages.put(id, message);
    eventIdQueue.addLast(id);

    trimIfNeeded();
    return id;
  }

  public SseMessage find(UUID eventId) {
    return messages.get(eventId);
  }

  /**
   * lastEventId 이후(미포함) 이벤트들을 순서대로 반환.
   * lastEventId가 null이면 빈 리스트 반환(또는 전체 replay를 원하면 정책 바꿀 수 있음)
   */
  public List<EventRecord> findAfter(UUID lastEventId) {
    if (lastEventId == null) return List.of();

    List<EventRecord> result = new ArrayList<>();
    boolean include = false;

    for (UUID id : eventIdQueue) {
      if (!include) {
        if (id.equals(lastEventId)) include = true;
        continue;
      }
      SseMessage msg = messages.get(id);
      if (msg != null) result.add(new EventRecord(id, msg));
    }
    return result;
  }

  private void trimIfNeeded() {
    while (eventIdQueue.size() > MAX_EVENTS) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) messages.remove(oldest);
    }
  }

  public record EventRecord(UUID id, SseMessage message) {}
}