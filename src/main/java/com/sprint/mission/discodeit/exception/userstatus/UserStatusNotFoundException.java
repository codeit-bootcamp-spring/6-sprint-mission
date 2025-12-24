package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserStatusNotFoundException extends UserStatusException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserStatusNotFoundException(UUID userStatusId) {
        super(
                ErrorCode.USER_STATUS_NOT_FOUND,
                "UserStatus를 찾을 수 없습니다. userStatusId=" + userStatusId,
                Map.of("userStatusId", userStatusId)
        );
    }

    public static UserStatusNotFoundException byUserId(UUID userId) {
        return new UserStatusNotFoundException (
                ErrorCode.USER_STATUS_NOT_FOUND,
                "해당 사용자에 대해 UserStatus가 존재하지 않습니다. userId=" + userId,
                Map.of("userId", userId)
        );
    }

    private UserStatusNotFoundException(ErrorCode code, String message, Map<String, Object> details) {
        super(code, message, details);
    }
}