package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.SseMessageEntity;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {

  private static final int DEFAULT_TIMEOUT = 1000 * 60 * 30;
  private static final int PAGE_SIZE = 50;
  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {

    SseEmitter sseEmitter = new SseEmitter((long) DEFAULT_TIMEOUT);

    sseEmitter.onCompletion(() -> {
      sseEmitterRepository.deleteByReceiverIdAndSseEmitter(receiverId, sseEmitter);
      log.info("SSE connection completed for receiverId: {}", receiverId);
    });

    sseEmitter.onTimeout(() -> {
      sseEmitterRepository.deleteByReceiverIdAndSseEmitter(receiverId, sseEmitter);
      log.info("SSE connection timed out for receiverId: {}", receiverId);
    });

    sseEmitter.onError((e) -> {
      sseEmitterRepository.deleteByReceiverIdAndSseEmitter(receiverId, sseEmitter);
      log.error("SSE connection error for receiverId: {}: {}", receiverId, e.getMessage());
    });

    sseEmitterRepository.save(receiverId, sseEmitter);

    // Send missed events if lastEventId is provided
    if (lastEventId != null) {

      log.info("Receiver {} reconnecting with lastEventId {}", receiverId, lastEventId);

      this.sendMissedEvents(sseEmitter, receiverId, lastEventId);

    } else {
      log.info("Receiver {} connecting for the first time", receiverId);
    }

    return sseEmitter;

  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {

    // Store the event
    SseMessageEntity sseMessageEntity = SseMessageEntity.createForPrivate(
        eventName,
        data,
        Set.copyOf(receiverIds)
    );

    sseMessageRepository.save(sseMessageEntity);

    // Send the event to each receiver
    receiverIds.forEach(receiverId -> {

      List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);

      if (emitters != null) {

        List<SseEmitter> deadEmitters = new ArrayList<>();

        emitters.forEach(emitter -> {
          try {
            emitter.send(SseEmitter.event()
                .name(eventName)
                .data(data)
                .id(sseMessageEntity.getEventId().toString()));
          } catch (IOException e) {
            log.error("IO Exception while sending SSE to receiverId {}: {}", receiverId, e.getMessage());
            deadEmitters.add(emitter);
          } catch (Exception e) {
            log.error("Failed to send SSE to receiverId {}: {}", receiverId, e.getMessage());
            deadEmitters.add(emitter);
          }
        });

        // Clean up dead emitters
        deadEmitters.forEach(deadEmitter ->
            sseEmitterRepository.deleteByReceiverIdAndSseEmitter(receiverId, deadEmitter));
      }
    });

  }

  public void sendMissedEvents(SseEmitter sseEmitter, UUID receiverId, UUID lastEventId) {

    List<SseMessageEntity> missedEvents = sseMessageRepository
        .findAllAfterEventIdAndReceiverId(lastEventId, receiverId);

    for (SseMessageEntity event : missedEvents) {
      try {
        sseEmitter.send(SseEmitter.event()
            .name(event.getEventName())
            .data(event.getData())
            .id(event.getEventId().toString()));
      } catch (IOException e) {
        log.error("IO Exception while sending SSE to receiverId {}: {}", receiverId, e.getMessage());
        return;
      } catch (Exception e) {
        log.error("Failed to send SSE to receiverId {}: {}", receiverId, e.getMessage());
        return;
      }

    }

  }

  public void broadcast(String eventName, Object data) {

    // Store the event
    SseMessageEntity sseMessageEntity = SseMessageEntity.createForAll(
        eventName,
        data
    );

    sseMessageRepository.save(sseMessageEntity);

    // Send the event to all connected emitters
    List<SseEmitter> allEmitters = sseEmitterRepository.findAll();
    List<SseEmitter> deadEmitters = new ArrayList<>();

    allEmitters.forEach(emitter -> {
      try {
        emitter.send(SseEmitter.event()
            .name(eventName)
            .data(data)
            .id(sseMessageEntity.getEventId().toString()));
      } catch (IOException e) {
        log.error("IO Exception while broadcasting SSE: {}", e.getMessage());
        deadEmitters.add(emitter);
      } catch (Exception e) {
        log.error("Failed to broadcast SSE: {}", e.getMessage());
        deadEmitters.add(emitter);
      }
    });

    // Clean up dead emitters
    sseEmitterRepository.deleteAllBySseEmitters(deadEmitters);

  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanupExpiredConnections() {

    int offset = 0;

    List<SseEmitter> pageOfEmitters = sseEmitterRepository.findAllByPage(0, PAGE_SIZE);
    List<SseEmitter> deadEmitters = new ArrayList<>();

    while (!pageOfEmitters.isEmpty()) {

      for (SseEmitter emitter : pageOfEmitters) {
        if (!ping(emitter)) {
          deadEmitters.add(emitter);
        }
      }

      offset++;
      pageOfEmitters = sseEmitterRepository.findAllByPage(offset, PAGE_SIZE);

    }

    sseEmitterRepository.deleteAllBySseEmitters(deadEmitters);

  }

  private boolean ping(SseEmitter sseEmitter) {

    try {
      sseEmitter.send(
          SseEmitter.event()
              .name("ping")
              .data("ping")
      );
    } catch (IOException e) {
      return false;
    }

    return true;

  }

}
