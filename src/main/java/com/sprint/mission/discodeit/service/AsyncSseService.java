package com.sprint.mission.discodeit.service;

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

@Service
@RequiredArgsConstructor
public class AsyncSseService {
    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository messageRepository;

    @Async("sseExecutor")
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        for (UUID receiverId : receiverIds) {
            List<SseEmitter> lists = emitterRepository.getEmitters(receiverId);
            lists.forEach(emitter -> {
                SseMessage message = new SseMessage(eventName, data);
                messageRepository.save(UUID.randomUUID(), message);
                try {
                    emitter.send(message);
                } catch (IOException e) {
                    emitter.complete();
                }
            });
        }
    }
}
