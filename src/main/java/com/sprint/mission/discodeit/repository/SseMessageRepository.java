package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {

    private static final int MAX_QUEUE_SIZE = 1000;

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public void save(UUID id, SseMessage message) {

        if (eventIdQueue.size() >= MAX_QUEUE_SIZE) {
            UUID removeId = eventIdQueue.pop();
            messages.remove(removeId);
        }

        eventIdQueue.push(id);
        messages.put(id, message);
    }

    public List<SseMessage> get(UUID id) {

        if (eventIdQueue.contains(id) == false
                || messages.containsKey(id) == false)
            return List.of();

        boolean isFound = false;
        List<SseMessage> result = new ArrayList<>();
        for (UUID eventId : eventIdQueue) {
            if (isFound)
                result.add(messages.get(eventId));

            if (eventId.equals(id)) {
                isFound = true;
            }
        }

        return result;
    }
}
