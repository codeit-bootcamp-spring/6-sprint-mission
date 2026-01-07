package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDTO;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<NotificationDTO> findAllNotificationsByUserId(UUID userId);

  void deleteNotificationById(UUID id, UUID userId);

}
