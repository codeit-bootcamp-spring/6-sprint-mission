package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.enums.ErrorCode;

import java.util.Map;

public abstract class MessageException extends DiscodeitException {
    protected MessageException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected MessageException(ErrorCode errorCode, String message) {
        super(errorCode, message, Map.of());
    }
}
