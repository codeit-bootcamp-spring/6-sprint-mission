package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.enums.ErrorCode;

import java.util.Map;

public abstract class UserException extends DiscodeitException {
    protected UserException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected UserException(ErrorCode errorCode, String message) {
        super(errorCode, message, Map.of());
    }
}
