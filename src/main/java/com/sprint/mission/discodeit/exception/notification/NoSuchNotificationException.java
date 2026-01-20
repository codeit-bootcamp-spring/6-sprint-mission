package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.NotificationException;
import java.util.Map;

public class NoSuchNotificationException extends NotificationException {

  public NoSuchNotificationException() {
    super(ErrorCode.NO_SUCH_NOTIFICATION, Map.of());
  }

  public NoSuchNotificationException(Map<String, Object> details) {
    super(ErrorCode.NO_SUCH_NOTIFICATION, details);
  }

}
