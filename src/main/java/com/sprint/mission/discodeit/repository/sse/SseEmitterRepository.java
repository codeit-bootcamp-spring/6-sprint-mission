package com.sprint.mission.discodeit.repository.sse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  // 수신자마다 연결 관리하는 맵
  private final Map<UUID, Map<UUID, SseEmitter>> data = new ConcurrentHashMap<>();

  public SseEmitter save(UUID receiverId, UUID connectionId, SseEmitter emitter) {
    data.computeIfAbsent(receiverId, k -> new ConcurrentHashMap<>())
        .put(connectionId, emitter);
    return emitter;
  }

  public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
    Map<UUID, SseEmitter> connections = data.get(receiverId);
    return (connections != null) ? List.copyOf(connections.values()) : List.of();
  }

  public Map<UUID, Map<UUID, SseEmitter>> findAll() {
    return Collections.unmodifiableMap(data);
  }

  public int countConnectionsByReceiverId(UUID receiverId) {
    Map<UUID, SseEmitter> connections = data.get(receiverId);
    return (connections != null) ? connections.size() : 0;
  }

  public void deleteByReceiverId(UUID receiverId) {
    data.remove(receiverId);
  }

  public boolean deleteByConnectionId(UUID receiverId, UUID connectionId) {
    Map<UUID, SseEmitter> connections = data.get(receiverId);
    if (connections == null) {
      return false;
    }
    SseEmitter removed = connections.remove(connectionId);
    if (removed != null) {
      // 수신자에 대한 연결이 더 이상 없으면 수신자 항목도 제거
      if (connections.isEmpty()) {
        data.remove(receiverId);
      }
      return true;
    }
    return false;
  }
}
