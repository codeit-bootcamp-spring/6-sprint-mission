package com.sprint.mission.discodeit.sse;

import java.util.ArrayList;
import java.util.Collection;
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
    data.compute(receiverId, (key, emitters) -> {
      List<SseEmitter> updated = emitters != null ? emitters : new CopyOnWriteArrayList<>();
      updated.add(emitter);
      return updated;
    });
  }

  public List<SseEmitter> findByReceiverId(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public Collection<SseEmitter> findAll() {
    List<SseEmitter> emitters = new ArrayList<>();
    for (List<SseEmitter> values : data.values()) {
      emitters.addAll(values);
    }
    return emitters;
  }

  public void remove(UUID receiverId, SseEmitter emitter) {
    data.computeIfPresent(receiverId, (key, emitters) -> {
      emitters.remove(emitter);
      return emitters.isEmpty() ? null : emitters;
    });
  }

  public void remove(SseEmitter emitter) {
    for (UUID receiverId : data.keySet()) {
      remove(receiverId, emitter);
    }
  }
}
