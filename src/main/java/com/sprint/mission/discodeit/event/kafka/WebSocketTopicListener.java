package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketTopicListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "discodeit-broadcast-${discodeit.kafka.broadcast-group-id}"
  )
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      MessageDto message = event.getData();
      String destination = String.format("/sub/channels.%s.messages", message.channelId());
      log.debug("WebSocket broadcast from Kafka: destination={}, messageId={}",
          destination, message.id());
      messagingTemplate.convertAndSend(destination, message);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
