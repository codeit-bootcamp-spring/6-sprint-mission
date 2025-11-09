package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusDuplicateException extends ReadStatusException {
    public ReadStatusDuplicateException() {
        super(ErrorCode.READ_STATUS_DUPLICATE);
    }
}