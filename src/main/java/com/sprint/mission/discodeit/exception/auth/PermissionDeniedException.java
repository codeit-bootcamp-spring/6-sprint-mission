package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.List;

public class PermissionDeniedException extends AuthException {
    public PermissionDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static PermissionDeniedException withRole(List<String> roles) {
        PermissionDeniedException exception = new PermissionDeniedException(ErrorCode.PERMISSION_DENIED);
        exception.addDetail("Role", roles.toString());
        return exception;
    }
}
