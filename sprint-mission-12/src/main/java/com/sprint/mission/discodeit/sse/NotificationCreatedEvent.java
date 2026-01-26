package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.UUID;

public record NotificationCreatedEvent(
    UUID receiverId,
    NotificationDto notificationDto
) {}
