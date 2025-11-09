package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class AuthLoginFailException extends AuthException {
    public AuthLoginFailException() {
        super(ErrorCode.AUTH_LOGIN_FAIL);
    }
}
