package com.sprint.mission.discodeit.repository;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Getter
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public SseEmitter save(UUID receiverId, long timeOut) {
        if (receiverId == null)
            throw new IllegalArgumentException("receiverId cannot be null");

        SseEmitter emitter = new SseEmitter(timeOut);

        if (data.containsKey(receiverId)) {
            List<SseEmitter> emitters = data.getOrDefault(receiverId, new ArrayList<>());
            emitters.add(emitter);
            data.put(receiverId, emitters);
        } else {
            List<SseEmitter> emitters = new ArrayList<>();
            emitters.add(emitter);
            data.put(receiverId, emitters);
        }

        return emitter;
    }

    public Integer getConnectionCount() {
        if (data.isEmpty())
            return 0;

        AtomicInteger count = new AtomicInteger(0);
        data.forEach((id, emitters) -> {
            emitters.forEach(emitter -> {
                count.addAndGet(1);
            });
        });

        return count.get();
    }

    public void remove(UUID id) {
        List<SseEmitter> emitters = data.remove(id);
        if (emitters != null) {
            emitters.forEach(x -> x.complete());
        }
    }

    public List<SseEmitter> getEmitters(UUID id) {
        if (data.containsKey(id))
            return data.get(id);

        return List.of();
    }
}
