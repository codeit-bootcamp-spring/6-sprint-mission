package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType,
        int status
) {

    public static ErrorResponse of(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        return new ErrorResponse(
                timestamp,
                errorCode.getStatus().toString(),
                errorCode.getMessage(),
                details,
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getStatus().value());
    }
}
