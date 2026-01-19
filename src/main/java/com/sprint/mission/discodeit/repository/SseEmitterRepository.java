package com.sprint.mission.discodeit.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@RequiredArgsConstructor
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public List<SseEmitter> findAll() {

    List<SseEmitter> allEmitters = new ArrayList<>();
    data.values().forEach(allEmitters::addAll);

    return allEmitters;

  }

  //pageable
  public List<SseEmitter> findAllByPage(int page, int size) {

    List<SseEmitter> allEmitters = new ArrayList<>();
    data.values().forEach(allEmitters::addAll);

    int fromIndex = page * size;
    int toIndex = Math.min(fromIndex + size, allEmitters.size());

    if (fromIndex > allEmitters.size()) {
      return new ArrayList<>();
    }

    return allEmitters.subList(fromIndex, toIndex);

  }

  public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
    return data.get(receiverId);
  }

  public void save(UUID receiverId, SseEmitter sseEmitter) {

    data.compute(receiverId, (key, emitters) -> {
      if (emitters == null) {
        emitters = new ArrayList<>();
      }
      emitters.add(sseEmitter);
      return emitters;
    });

  }

  public void deleteAllBySseEmitters(List<SseEmitter> sseEmitters) {

    for (List<SseEmitter> emitters : data.values()) {
      emitters.removeAll(sseEmitters);
    }

  }

  public void deleteByReceiverIdAndSseEmitter(UUID receiverId, SseEmitter sseEmitter) {
    List<SseEmitter> emitters = data.get(receiverId);
    if (emitters != null) {
      emitters.remove(sseEmitter);
      if (emitters.isEmpty()) {
        data.remove(receiverId);
      }
    }
  }

}
