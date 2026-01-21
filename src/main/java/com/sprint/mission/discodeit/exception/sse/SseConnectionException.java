package com.sprint.mission.discodeit.exception.sse;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class SseConnectionException extends SseException {

  public SseConnectionException() {
    super(ErrorCode.SSE_CONNECTION_FAILED);
  }
}
