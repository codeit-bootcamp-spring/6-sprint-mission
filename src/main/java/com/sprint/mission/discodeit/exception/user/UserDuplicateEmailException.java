package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserDuplicateEmailException extends UserException{
    public UserDuplicateEmailException(){
        super(ErrorCode.USER_DUPLICATE_EMAIL);
    }
}
