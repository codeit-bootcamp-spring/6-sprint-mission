package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class NoSuchNotificationException extends DiscodeitException {

  public NoSuchNotificationException() {
    super(ErrorCode.NO_SUCH_NOTIFICATION, Map.of());
  }

  public NoSuchNotificationException(Map<String, Object> details) {
    super(ErrorCode.NO_SUCH_NOTIFICATION, details);
  }

}
