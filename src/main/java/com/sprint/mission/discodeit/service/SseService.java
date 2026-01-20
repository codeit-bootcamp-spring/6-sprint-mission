package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.SseMessage;
import com.sprint.mission.discodeit.repository.SseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {

    private final SseRepository sseRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 30; // 30분

    // SseEmitter 객체 생성
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        // 1. 새로운 Emitter 생성
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 2. 연결 종료/타임아웃 시 레포지토리에서 삭제하도록 설정
        emitter.onCompletion(() -> sseRepository.deleteEmitter(receiverId, emitter));
        emitter.onTimeout(() -> sseRepository.deleteEmitter(receiverId, emitter));
        emitter.onError((e) -> sseRepository.deleteEmitter(receiverId, emitter));

        // 3. 레포지토리에 저장
        sseRepository.saveEmitter(receiverId, emitter);

        // 4. 연결 즉시 더미 이벤트를 보내 503 에러 방지 및 연결 확인
        ping(emitter, receiverId, "connect check");

        // 5. 클라이언트가 마지막으로 받은 ID가 있다면, 그 이후의 유실된 메시지 재전송
        if (lastEventId != null) {
            SseMessage lastMessage = sseRepository.findLastMessageByUserId(receiverId);
            if (lastMessage != null) {
                sendToClient(emitter, receiverId, "re-delivery", lastMessage);
            }
        }

        return emitter;
    }

    // SseEmitter 객체를 통해 다수의 사용자에게 이벤트를 전송
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        receiverIds.forEach(id -> {
            SseMessage sseMessage = SseMessage.create(eventName, data);
            sseRepository.saveMessage(id, sseMessage); // 유실 복구용 저장

            List<SseEmitter> emitters = sseRepository.findEmittersByUserId(id);
            emitters.forEach(emitter -> sendToClient(emitter, id, eventName, data));
        });
    }

    // SseEmitter 객체를 통해 접속중인 모든 사용자에게 이벤트를 전송
    public void broadcast(String eventName, Object data) {
        log.info("Broadcasting event: {}", eventName);

    }

    // 만료된 SseEmitter 객체를 삭제
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        // sseRepository를 순회하며 삭제
        log.info("SSE Emitter clean up task started.");
    }

    // 최초 연결 또는 만료 여부 확인 위해 더미 이벤트를 보냄
    private void ping(SseEmitter sseEmitter, UUID userId, String message) {
        sendToClient(sseEmitter, userId, "ping", message);
    }

    private void sendToClient(SseEmitter emitter, UUID userId, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            sseRepository.deleteEmitter(userId, emitter);
            log.error("SSE 전송 실패로 인한 연결 삭제: {}", userId);
        }
    }
}
