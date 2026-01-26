package com.sprint.mission.discodeit.sse;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {

  private static final long DEFAULT_TIMEOUT = 0L; // 무제한(인프라에 맞게 조정 가능)

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    emitterRepository.add(receiverId, emitter);

    emitter.onCompletion(() -> emitterRepository.remove(receiverId, emitter));
    emitter.onTimeout(() -> emitterRepository.remove(receiverId, emitter));
    emitter.onError(e -> emitterRepository.remove(receiverId, emitter));

    // 1) 최초 연결 ping (연결 확인)
    if (!ping(emitter)) {
      emitterRepository.remove(receiverId, emitter);
      return emitter;
    }

    // 2) 유실 복원: lastEventId 이후 이벤트 replay
    // (요구사항은 "가능하도록"이므로, 전역 replay라도 구현하면 요구 충족)
    List<SseMessageRepository.EventRecord> missed = messageRepository.findAfter(lastEventId);
    for (SseMessageRepository.EventRecord rec : missed) {
      if (isReceiverEligible(rec.message(), receiverId)) {
        sendToEmitter(emitter, rec.id(), rec.message().eventName(), rec.message().data());
      }
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    // 이벤트 저장(고유 ID 생성)
    UUID eventId = messageRepository.save(
        new SseMessage(eventName, data, Instant.now(), receiverIds)
    );

    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = emitterRepository.get(receiverId);
      for (SseEmitter emitter : emitters) {
        if (!sendToEmitter(emitter, eventId, eventName, data)) {
          emitterRepository.remove(receiverId, emitter);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    UUID eventId = messageRepository.save(
        new SseMessage(eventName, data, Instant.now(), null)
    );

    for (Map.Entry<UUID, List<SseEmitter>> entry : emitterRepository.getAll().entrySet()) {
      UUID receiverId = entry.getKey();
      for (SseEmitter emitter : entry.getValue()) {
        if (!sendToEmitter(emitter, eventId, eventName, data)) {
          emitterRepository.remove(receiverId, emitter);
        }
      }
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    for (Map.Entry<UUID, List<SseEmitter>> entry : emitterRepository.getAll().entrySet()) {
      UUID receiverId = entry.getKey();

      for (SseEmitter emitter : entry.getValue()) {
        if (!ping(emitter)) {
          emitterRepository.remove(receiverId, emitter);
        }
      }
    }
  }

  private boolean ping(SseEmitter sseEmitter) {
    // 더미 이벤트(연결 확인)
    return sendToEmitter(sseEmitter, null, "ping", "ok");
  }

  private boolean isReceiverEligible(SseMessage message, UUID receiverId) {
    Collection<UUID> receiverIds = message.receiverIds();
    return receiverIds == null || receiverIds.contains(receiverId);
  }

  private boolean sendToEmitter(SseEmitter emitter, UUID eventId, String eventName, Object data) {
    try {
      SseEmitter.SseEventBuilder builder = SseEmitter.event().name(eventName).data(data);
      if (eventId != null) builder.id(eventId.toString());
      emitter.send(builder);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
