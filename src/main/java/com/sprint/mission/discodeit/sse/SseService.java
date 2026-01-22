package com.sprint.mission.discodeit.sse;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

  private static final long DEFAULT_TIMEOUT = 0L;

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    emitterRepository.add(receiverId, emitter);

    emitter.onCompletion(() -> emitterRepository.remove(receiverId, emitter));
    emitter.onTimeout(() -> emitterRepository.remove(receiverId, emitter));
    emitter.onError(error -> emitterRepository.remove(receiverId, emitter));

    if (!ping(emitter)) {
      emitterRepository.remove(receiverId, emitter);
      return emitter;
    }

    List<SseMessage> missedMessages = messageRepository.findAfter(receiverId, lastEventId);
    for (SseMessage message : missedMessages) {
      sendToEmitter(emitter, message);
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    if (receiverIds == null || receiverIds.isEmpty()) {
      return;
    }

    for (UUID receiverId : receiverIds) {
      SseMessage message = messageRepository.save(
          new SseMessage(
              UUID.randomUUID(),
              eventName,
              data,
              receiverId,
              Instant.now()
          )
      );
      for (SseEmitter emitter : emitterRepository.findByReceiverId(receiverId)) {
        sendToEmitter(emitter, message);
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = messageRepository.save(
        new SseMessage(
            UUID.randomUUID(),
            eventName,
            data,
            null,
            Instant.now()
        )
    );
    for (SseEmitter emitter : emitterRepository.findAll()) {
      sendToEmitter(emitter, message);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    for (SseEmitter emitter : emitterRepository.findAll()) {
      if (!ping(emitter)) {
        emitterRepository.remove(emitter);
      }
    }
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .name("ping")
          .data("ping"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void sendToEmitter(SseEmitter emitter, SseMessage message) {
    try {
      emitter.send(SseEmitter.event()
          .id(message.id().toString())
          .name(message.eventName())
          .data(message.data()));
    } catch (IOException e) {
      emitter.complete();
      emitterRepository.remove(emitter);
    }
  }
}
