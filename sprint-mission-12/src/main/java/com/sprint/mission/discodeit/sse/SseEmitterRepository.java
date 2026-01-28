package com.sprint.mission.discodeit.sse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public void add(UUID receiverId, SseEmitter emitter) {
    data.computeIfAbsent(receiverId, k -> new CopyOnWriteArrayList<>()).add(emitter);
  }

  public List<SseEmitter> get(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public void remove(UUID receiverId, SseEmitter emitter) {
    List<SseEmitter> list = data.get(receiverId);
    if (list == null) return;

    list.remove(emitter);
    if (list.isEmpty()) data.remove(receiverId);
  }

  public ConcurrentMap<UUID, List<SseEmitter>> getAll() {
    return data;
  }
}