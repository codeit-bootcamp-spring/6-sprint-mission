package com.sprint.mission.discodeit.exception.sse;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class SseException extends DiscodeitException {

  public SseException(ErrorCode errorCode) {
    super(errorCode);
  }
}
