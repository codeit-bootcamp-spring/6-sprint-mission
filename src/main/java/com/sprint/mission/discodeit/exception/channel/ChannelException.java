package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.enums.ErrorCode;

import java.util.Map;

public abstract class ChannelException extends DiscodeitException {
    protected ChannelException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected ChannelException(ErrorCode errorCode, String message) {
        super(errorCode, message, Map.of());
    }
}
