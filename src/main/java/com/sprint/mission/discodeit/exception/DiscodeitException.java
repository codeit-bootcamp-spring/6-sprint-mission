package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class DiscodeitException  extends RuntimeException{
    final Instant timestamp;
    final ErrorCode errorCode;
    final Map<String, Object> details;

    public DiscodeitException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.details = details;
    }
}
