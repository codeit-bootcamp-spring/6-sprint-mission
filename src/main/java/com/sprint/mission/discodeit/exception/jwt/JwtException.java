package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.enums.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.Map;

public abstract class JwtException extends DiscodeitException {
    public JwtException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
