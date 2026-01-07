package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.NotificationEntity;
import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;

  @Async
  @Transactional
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessageCreatedEvent(MessageCreatedEvent event) {

    List<UUID> userIdList = readStatusRepository.findByChannelId(event.getChannelId())
        .stream()
        .map(readStatus -> readStatus.getUser().getId())
        .toList();

    List<NotificationEntity> notifications = userIdList.stream().map(userId ->
        NotificationEntity.builder()
          .receiverId(userId)
          .title("New Message in Channel " + event.getChannelId())
          .content("A new message with ID " + event.getMessageId() + " has been created.")
          .build()
    ).toList();

    notificationRepository.saveAll(notifications);

  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleRoleUpdatedEvent(RoleUpdatedEvent event) {

    NotificationEntity notification = NotificationEntity.builder()
        .receiverId(event.getUserId())
        .title("Role Updated")
        .content("Your role has been updated to " + event.getNewRole() + ".")
        .build();

    notificationRepository.save(notification);

  }

}
