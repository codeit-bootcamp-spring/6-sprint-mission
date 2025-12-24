package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.enums.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserStatusAlreadyExistsException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserStatusAlreadyExistsException(UUID userId) {
        super(
                ErrorCode.DUPLICATE_USER_STATUS,
                "해당 사용자에 대해 UserStatus가 이미 존재합니다. userId=" + userId,
                Map.of("userId", userId)
        );
    }

}
