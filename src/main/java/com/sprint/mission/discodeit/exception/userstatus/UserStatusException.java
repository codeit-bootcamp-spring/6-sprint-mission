package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.enums.ErrorCode;

import java.util.Map;

public abstract class UserStatusException extends DiscodeitException {

    protected UserStatusException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected UserStatusException(ErrorCode errorCode, String message) {
        super(errorCode, message, Map.of());
    }
}
