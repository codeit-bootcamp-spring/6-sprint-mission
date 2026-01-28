package com.sprint.mission.discodeit.exception.sse;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class SseServerFullException extends SseException {

  public SseServerFullException() {
    super(ErrorCode.SSE_CAPACITY_EXCEEDED);
  }
}
