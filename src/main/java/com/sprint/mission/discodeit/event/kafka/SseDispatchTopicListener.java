package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.sse.SseDispatchEvent;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SseDispatchTopicListener {

  private final SseService sseService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.SseDispatchEvent",
      groupId = "discodeit-broadcast-${discodeit.kafka.broadcast-group-id}"
  )
  public void onSseDispatchEvent(String kafkaEvent) {
    try {
      SseDispatchEvent event = objectMapper.readValue(kafkaEvent, SseDispatchEvent.class);
      Collection<UUID> receiverIds = event.getReceiverIds();

      if (receiverIds == null || receiverIds.isEmpty()) {
        log.debug("SSE broadcast from Kafka: name={}", event.getEventName());
        sseService.broadcast(event.getEventName(), event.getData());
        return;
      }

      log.debug("SSE dispatch from Kafka: name={}, receiverCount={}",
          event.getEventName(), receiverIds.size());
      sseService.send(receiverIds, event.getEventName(), event.getData());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
