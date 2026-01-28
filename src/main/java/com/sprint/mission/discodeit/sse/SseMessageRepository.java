package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.sse.dto.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Repository
public class SseMessageRepository {
    private static final int MAX_CACHE_SIZE = 100;

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public void save(UUID eventId, SseMessage message) {
        messages.put(eventId, message);
        eventIdQueue.offer(eventId); // 큐의 맨 뒤에 추가

        // ★ 핵심: 용량이 꽉 찼으면 제일 오래된 것 삭제
        if (eventIdQueue.size() > MAX_CACHE_SIZE) {
            delete();
        }
    }

    public List<SseMessage> findAllAfter(UUID lastEventId) {
        return messages.values().stream()
                .filter(msg -> msg.id().compareTo(lastEventId) > 0)
                .collect(Collectors.toList());
    }

    public void delete(){
        UUID id = eventIdQueue.poll();
        if(id != null){
            messages.remove(id);
        }
    }

}
