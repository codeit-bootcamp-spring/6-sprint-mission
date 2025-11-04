package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

  public static ErrorResponse of(DiscodeitException ex, ErrorCode errorCode) {
    return new ErrorResponse(
        ex.getTimestamp(),
        errorCode.getCode(),
        errorCode.getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        errorCode.getStatus()
    );
  }

  public static ErrorResponse error(Exception ex, ErrorCode errorCode) {
    return new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        null,
        ex.getClass().getSimpleName(),
        errorCode.getStatus()
    );
  }
}
