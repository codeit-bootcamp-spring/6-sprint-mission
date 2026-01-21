package com.sprint.mission.discodeit.event.listener;

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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Async("eventTaskExecutor")
  //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessageCreatedEvent(MessageCreatedEvent event) {

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

  }

  @Async("eventTaskExecutor")
  //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleRoleUpdatedEvent(RoleUpdatedEvent event) {

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

  }

  @Async("eventTaskExecutor")
  //@EventListener
  public void handleFileUploadFailedEvent(FileUploadFailedEvent event) {

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

  }

}
