package com.sprint.mission.discodeit.exception.sse;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class SseUserSessionLimitException extends SseException {

  public SseUserSessionLimitException() {
    super(ErrorCode.SSE_TOO_MANY_CONNECTIONS);
  }
}
