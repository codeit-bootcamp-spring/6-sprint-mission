package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.model.NotificationDto;
import com.sprint.mission.discodeit.dto.request.CreateMessageNotificationRequest;
import com.sprint.mission.discodeit.dto.request.UpdateRoleNotificationRequest;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  void createMessageNotification(CreateMessageNotificationRequest request);

  void createRoleUpdateNotification(UpdateRoleNotificationRequest request);

  List<NotificationDto> getMessageNotifications(UUID userId);

  void deleteNotification(UUID notificationId, UUID userId);
}
