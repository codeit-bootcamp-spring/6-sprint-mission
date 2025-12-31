package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.enums.ErrorCode;

import java.util.Map;

public abstract class BinaryContentException extends DiscodeitException {

    protected BinaryContentException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected BinaryContentException(ErrorCode errorCode, String message) {
            super(errorCode, message, Map.of());
    }
}
