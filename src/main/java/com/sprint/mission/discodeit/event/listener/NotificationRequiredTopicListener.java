package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.NotificationEntity;
import com.sprint.mission.discodeit.event.event.CacheClearEvent;
import com.sprint.mission.discodeit.event.event.FileUploadFailedEvent;
import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.message.NoSuchMessageException;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final NotificationRepository notificationRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Async
  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {

    try {

      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
          MessageCreatedEvent.class);

      MessageEntity message = messageRepository.findById(event.getMessageId())
          .orElseThrow(NoSuchMessageException::new);

      List<UUID> userIdList = readStatusRepository.findByChannelId(event.getChannelId())
          .stream()
          .map(readStatus -> readStatus.getUser().getId())
          .filter(userId -> !userId.equals(message.getAuthor().getId()))
          .toList();

      List<NotificationEntity> notifications = userIdList.stream().map(userId ->
          NotificationEntity.builder()
              .receiverId(userId)
              .title("New Message in Channel " + event.getChannelId())
              .content("A new message with ID " + event.getMessageId() + " has been created.")
              .build()
      ).toList();

      notificationRepository.saveAll(notifications);

      eventPublisher.publishEvent(
          CacheClearEvent.RenewNotificationByUserIdCacheEvent.of(userIdList)
      );

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

  }

  @Async
  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {

    try {

      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
          RoleUpdatedEvent.class);

      NotificationEntity notification = NotificationEntity.builder()
          .receiverId(event.getUserId())
          .title("Role Updated")
          .content("Your role has been updated to " + event.getNewRole() + ".")
          .build();

      notificationRepository.save(notification);

      eventPublisher.publishEvent(
          CacheClearEvent.RenewNotificationByUserIdCacheEvent.of(
              List.of(event.getUserId())
          )
      );

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

  }

  @Async
  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {

    try {

      FileUploadFailedEvent event = objectMapper.readValue(kafkaEvent,
          FileUploadFailedEvent.class);

      NotificationEntity notification = NotificationEntity.builder()
          .receiverId(userRepository.findByUsername("admin")
              .orElseThrow(NoSuchUserException::new).getId())
          .title("S3 File Upload Failed")
          .content(
              "Request ID: " + event.getRequestId() +
                  ", Binary Content ID: " + event.getBinaryContentId() +
                  ", Error Message: " + event.getErrorMessage()
          )
          .build();

      notificationRepository.save(notification);

      eventPublisher.publishEvent(
          CacheClearEvent.RenewNotificationByUserIdCacheEvent.of(
              List.of(notification.getReceiverId())
          )
      );

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

  }

}
