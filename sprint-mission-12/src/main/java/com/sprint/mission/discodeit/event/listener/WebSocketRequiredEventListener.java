package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "discodeit.MessageCreatedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void handleMessage(String kafkaEvent) {
    MessageCreatedEvent event;
    try {
      event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    MessageDto message = event.getData();

    UUID channelId = message.channelId();

    String destination = "/sub/channels." + channelId + ".messages";

    messagingTemplate.convertAndSend(destination, message);
  }
}
