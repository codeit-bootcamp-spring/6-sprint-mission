package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.request.CreateMessageNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateRoleNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateStorageNotificationRequest;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationTaskHandler {

  private final NotificationService notificationService;

  public void createMessageNotificationTask(CreateMessageNotificationRequest request) {

    notificationService.createMessageNotification(request);
  }

  public void createRoleUpdateNotificationTask(CreateRoleNotificationRequest request) {

    notificationService.createRoleUpdateNotification(request);
  }

  public void createStorageNotificationTask(CreateStorageNotificationRequest request) {

    notificationService.createStorageNotification(request);
  }
}
