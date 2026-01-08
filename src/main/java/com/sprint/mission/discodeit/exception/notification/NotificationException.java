package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.enums.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

public abstract class NotificationException extends DiscodeitException {
    protected NotificationException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
