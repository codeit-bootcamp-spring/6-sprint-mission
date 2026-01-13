package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.request.CreateMessageNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateRoleNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateStorageNotificationRequest;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.StoragePutFailedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationTaskHandler {

  private final NotificationService notificationService;

  public void createMessageNotificationTask(MessageCreatedEvent event) {

    CreateMessageNotificationRequest request = CreateMessageNotificationRequest.builder()
        .channelId(event.channelId())
        .messageId(event.messageId())
        .authorId(event.authorId())
        .content(event.content())
        .build();

    notificationService.createMessageNotification(request);
  }

  public void createRoleUpdateNotificationTask(RoleUpdatedEvent event) {

    CreateRoleNotificationRequest request = CreateRoleNotificationRequest.builder()
        .userId(event.userId())
        .oldRole(event.oldRole())
        .newRole(event.newRole())
        .build();

    notificationService.createRoleUpdateNotification(request);
  }

  public void createStorageNotificationTask(StoragePutFailedEvent event) {

    CreateStorageNotificationRequest request = CreateStorageNotificationRequest.builder()
        .binaryContentId(event.binaryContentId())
        .errorType(event.errorType())
        .errorMessage(event.errorMessage())
        .build();

    notificationService.createStorageNotification(request);
  }
}
