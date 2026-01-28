package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.NotificationException;
import java.util.Map;

public class ForbiddenNotificationAccessException extends NotificationException {

  public ForbiddenNotificationAccessException() {
    super(ErrorCode.FORBIDDEN_NOTIFICATION_ACCESS, Map.of());
  }

  public ForbiddenNotificationAccessException(Map<String, Object> details) {
    super(ErrorCode.FORBIDDEN_NOTIFICATION_ACCESS, details);
  }
}
