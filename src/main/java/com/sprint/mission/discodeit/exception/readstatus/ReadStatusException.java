package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.enums.ErrorCode;

import java.util.Map;

public abstract class ReadStatusException extends DiscodeitException {
    protected ReadStatusException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected ReadStatusException(ErrorCode errorCode, String message) {
        super(errorCode, message, Map.of());
    }
}
