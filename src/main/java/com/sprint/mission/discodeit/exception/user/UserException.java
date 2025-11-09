package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;

public class UserException extends DiscodeitException {

    public UserException(ErrorCode errorCode) {
        super(Instant.now(),errorCode,Map.of());
    }
}
