package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserUpdateFailException extends UserException{

    public UserUpdateFailException() {
        super(ErrorCode.USER_UPDATE_FAIL);
    }
}