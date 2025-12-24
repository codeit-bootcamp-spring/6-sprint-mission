package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.enums.ErrorCode;

import java.time.Instant;
import java.util.Map;

public class DiscodeitException extends RuntimeException {

    public final Instant timestamp;
    public final ErrorCode errorCode;
    public final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }
}
