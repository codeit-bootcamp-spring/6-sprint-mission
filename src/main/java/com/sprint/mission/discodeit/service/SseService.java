package com.sprint.mission.discodeit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository messageRepository;
    private final AsyncSseService asyncSseService;

    private static final long TIMEOUT = 60 * 60 * 1000;
    private static final int MAX_CONNECTION = 1000;

    public SseEmitter connect(UUID recieverId, UUID lastEventId) {

        if (emitterRepository.getConnectionCount() >= MAX_CONNECTION)
            throw new IllegalStateException("Max connection count reached");

        SseEmitter emitter = emitterRepository.save(recieverId, TIMEOUT);
        emitter.onTimeout(() -> emitterRepository.remove(recieverId));
        emitter.onCompletion(() -> emitterRepository.remove(recieverId));
        emitter.onError((e) -> emitterRepository.remove(recieverId));

        //재연결시 후속 이벤트 전송
        if (lastEventId != null) {
            List<SseMessage> messages = messageRepository.get(lastEventId);
            messages.forEach(message -> send(List.of(recieverId), message.getId(), message.getData()));
        }

        return emitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        asyncSseService.send(receiverIds, eventName, data);
    }

    public void broadcast(String eventName, Object data) {
        send(emitterRepository.getData().keySet(), eventName, data);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        broadcast("heartbeat", "ping");
    }
}
