package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.SseMessage;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SseRepository {

    // SseEmitter 저장소
    private final ConcurrentMap<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>(); // List: 다중 연결 허용

    // SseMessage 저장소 (이벤트 유실 복원을 위한)
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    /**
     * SseEmitter 관련 헬퍼 메서드
     */
    public void saveEmitter(UUID userId, SseEmitter emitter) {
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public List<SseEmitter> findEmittersByUserId(UUID userId) {
        return emitters.getOrDefault(userId, Collections.emptyList());
    }

    // SSE 연결 종료 시 사용
    public void deleteEmitter(UUID userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId); // 리스트가 비어있으면 Map에서 삭제
            }
        }
    }

    /**
     * SseMessage 관련 헬퍼 메서드
     */
    public void saveMessage(UUID userId, SseMessage message) {
        messages.put(userId, message);
    }

    public SseMessage findLastMessageByUserId(UUID userId) {
        return messages.get(userId);
    }

    // 로그아웃 시 사용자의 모든 SSE 정보 삭제
    public void deleteAllByUserId(UUID userId) {
        emitters.remove(userId);
        messages.remove(userId);
    }
}
