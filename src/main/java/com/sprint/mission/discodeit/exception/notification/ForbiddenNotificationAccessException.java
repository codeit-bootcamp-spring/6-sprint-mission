package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ForbiddenNotificationAccessException extends DiscodeitException {

  public ForbiddenNotificationAccessException() {
    super(ErrorCode.FORBIDDEN_NOTIFICATION_ACCESS, Map.of());
  }

  public ForbiddenNotificationAccessException(Map<String, Object> details) {
    super(ErrorCode.FORBIDDEN_NOTIFICATION_ACCESS, details);
  }
}
