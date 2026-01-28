package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.model.SseMessage;
import com.sprint.mission.discodeit.exception.sse.SseConnectionException;
import com.sprint.mission.discodeit.exception.sse.SseServerFullException;
import com.sprint.mission.discodeit.exception.sse.SseUserSessionLimitException;
import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {

  public static final long TIMEOUT = 60L * 1000L * 60L;   // 1 hour
  public static final int MAX_CONNECTIONS_PER_RECEIVER = 5;
  public static final int MESSAGE_TTL_MINUTES = 60;
  private static final int MAX_TOTAL_CONNECTIONS = 10000;
  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;
  private final AtomicInteger connectionCount = new AtomicInteger(0);

  @Override
  public SseEmitter connect(UUID receiverId, UUID lastEventId) {

    if (connectionCount.get() >= MAX_TOTAL_CONNECTIONS) {
      throw new SseServerFullException();
    }

    if (sseEmitterRepository.countConnectionsByReceiverId(receiverId) >= MAX_CONNECTIONS_PER_RECEIVER) {
      throw new SseUserSessionLimitException();
    }

    connectionCount.incrementAndGet();
    SseEmitter emitter = new SseEmitter(TIMEOUT);
    UUID connectionId = UUID.randomUUID();

    Runnable cleanup = () -> {
      // 연결이 있을때만 감소
      if (sseEmitterRepository.deleteByConnectionId(receiverId, connectionId)) {
        connectionCount.decrementAndGet();
      }
    };

    emitter.onCompletion(cleanup);
    emitter.onTimeout(cleanup);
    emitter.onError(e -> cleanup.run());

    sseEmitterRepository.save(receiverId, connectionId, emitter);

    try {
      emitter.send(SseEmitter.event()
          .name("connect")
          .data("connected!"));

      if (lastEventId != null) {
        sseMessageRepository.deleteUpTo(lastEventId);
        List<SseMessage> missedMessages = sseMessageRepository.findAllByReceiverId(receiverId);
        for (SseMessage message : missedMessages) {
          emitter.send(SseEmitter.event()
              .id(message.id().toString())
              .name(message.eventName())
              .data(message.data()));
        }
      }
    } catch (Exception e) {
      cleanup.run();
      throw new SseConnectionException();
    }

    return emitter;
  }

  @Override
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {

    for (UUID receiverId : receiverIds) {
      SseMessage message = new SseMessage(
          UUID.randomUUID(),
          receiverId,
          eventName,
          data,
          LocalDateTime.now()
      );
      sseMessageRepository.save(message);

      List<SseEmitter> connections = sseEmitterRepository.findAllByReceiverId(receiverId);
      for (SseEmitter emitter : connections) {
        try {
          emitter.send(SseEmitter.event()
              .id(message.id().toString())
              .name(eventName)
              .data(data));
        } catch (Exception e) {
          log.warn("SSE 전송 실패, 수신자 ID: {}, 이벤트 이름: {}", receiverId, eventName);
        }
      }
    }
  }

  @Override
  public void broadcast(String eventName, Object data) {

    Map<UUID, Map<UUID, SseEmitter>> allData = sseEmitterRepository.findAll();

    for (UUID receiverId : allData.keySet()) {
      send(List.of(receiverId), eventName, data);
    }
  }

  // 30분마다 1시간 지난 메시지 삭제
  @Override
  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    sseMessageRepository.deleteOlderThan(LocalDateTime.now().minusMinutes(MESSAGE_TTL_MINUTES));
  }
}
