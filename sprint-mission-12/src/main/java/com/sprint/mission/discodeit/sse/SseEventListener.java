package com.sprint.mission.discodeit.sse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseEventListener {

  private final SseService sseService;
  private final ObjectMapper objectMapper;

  // 1) notifications.created (개인 알림)
  @KafkaListener(
      topics = "discodeit.NotificationCreatedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void onNotificationCreated(String kafkaEvent) {
    NotificationCreatedEvent event = readEvent(kafkaEvent, NotificationCreatedEvent.class);
    UUID receiverId = event.receiverId();
    sseService.send(List.of(receiverId), "notifications.created", event.notificationDto());
  }

  // 2) binaryContents.updated (업로드 상태 변경: 보통 업로더/관련자에게만)
  @KafkaListener(
      topics = "discodeit.BinaryContentUpdatedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void onBinaryContentUpdated(String kafkaEvent) {
    BinaryContentUpdatedEvent event = readEvent(kafkaEvent, BinaryContentUpdatedEvent.class);
    sseService.broadcast("binaryContents.updated", event.binaryContentDto());
  }

  // 3) channels.created/updated/deleted (보통 전체에 영향)
  @KafkaListener(
      topics = "discodeit.ChannelCreatedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void onChannelCreated(String kafkaEvent) {
    ChannelCreatedEvent event = readEvent(kafkaEvent, ChannelCreatedEvent.class);
    sseService.broadcast("channels.created", event.channelDto());
  }

  @KafkaListener(
      topics = "discodeit.ChannelUpdatedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void onChannelUpdated(String kafkaEvent) {
    ChannelUpdatedEvent event = readEvent(kafkaEvent, ChannelUpdatedEvent.class);
    sseService.broadcast("channels.updated", event.channelDto());
  }

  @KafkaListener(
      topics = "discodeit.ChannelDeletedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void onChannelDeleted(String kafkaEvent) {
    ChannelDeletedEvent event = readEvent(kafkaEvent, ChannelDeletedEvent.class);
    sseService.broadcast("channels.deleted", event.channelDto());
  }

  // 4) users.created/updated/deleted (보통 전체에 영향)
  @KafkaListener(
      topics = "discodeit.UserCreatedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void onUserCreated(String kafkaEvent) {
    UserCreatedEvent event = readEvent(kafkaEvent, UserCreatedEvent.class);
    sseService.broadcast("users.created", event.userDto());
  }

  @KafkaListener(
      topics = "discodeit.UserUpdatedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void onUserUpdated(String kafkaEvent) {
    UserUpdatedEvent event = readEvent(kafkaEvent, UserUpdatedEvent.class);
    sseService.broadcast("users.updated", event.userDto());
  }

  @KafkaListener(
      topics = "discodeit.UserDeletedEvent",
      groupId = "${discodeit.kafka.consumer-group-id.broadcast}"
  )
  public void onUserDeleted(String kafkaEvent) {
    UserDeletedEvent event = readEvent(kafkaEvent, UserDeletedEvent.class);
    sseService.broadcast("users.deleted", event.userDto());
  }

  private <T> T readEvent(String kafkaEvent, Class<T> type) {
    try {
      return objectMapper.readValue(kafkaEvent, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
