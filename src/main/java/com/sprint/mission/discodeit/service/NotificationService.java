package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void create(UUID userId, String title, String content);
    List<NotificationDto> list(UUID userId);
    void delete(UUID notificationId);
}
