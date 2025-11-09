package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusDuplicateException extends UserStatusException{

    public UserStatusDuplicateException() {
        super(ErrorCode.READ_STATUS_DUPLICATE);
    }
}