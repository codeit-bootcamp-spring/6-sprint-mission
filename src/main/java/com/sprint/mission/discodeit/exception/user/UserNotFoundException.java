package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L; // 파일 저장(직렬화) 시 충돌 방지를 위해 사용.

    public UserNotFoundException(UUID userId) {
        super(
                ErrorCode.USER_NOT_FOUND,
                "사용자를 찾을 수 없습니다. userId=" + userId,
                Map.of("userId", userId)
        );
    }

    public UserNotFoundException(String username) {
        super(
                ErrorCode.USER_NOT_FOUND,
                "사용자를 찾을 수 없습니다. username=" + username,
                Map.of("username", username)
        );
    }
}
