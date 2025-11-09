package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserDuplicateNameException extends UserException{
    public UserDuplicateNameException(){
        super(ErrorCode.USER_DUPLICATE_NAVE);
    }
}