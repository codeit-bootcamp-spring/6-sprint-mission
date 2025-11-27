package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends DiscodeitException {

    public BinaryContentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
