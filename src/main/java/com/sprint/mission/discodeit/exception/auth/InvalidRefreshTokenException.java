package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;

public class InvalidRefreshTokenException extends DiscodeitException {
    public InvalidRefreshTokenException(String message) {
        super(Instant.now(), ErrorCode.INVALID_AUTH, Map.of("err", message));
    }
}
