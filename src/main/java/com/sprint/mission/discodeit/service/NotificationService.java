package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.model.NotificationDto;
import com.sprint.mission.discodeit.dto.request.CreateMessageNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateRoleNotificationRequest;
import com.sprint.mission.discodeit.dto.request.CreateStorageNotificationRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

public interface NotificationService {

  void createMessageNotification(CreateMessageNotificationRequest request);

  void createRoleUpdateNotification(CreateRoleNotificationRequest request);

  List<NotificationDto> getMessageNotifications(UUID userId);

  void createStorageNotification(CreateStorageNotificationRequest request);

  @PreAuthorize("@basicAuthService.isOwner(#userId, principal)")
  void deleteNotification(UUID notificationId, UUID userId);
}
