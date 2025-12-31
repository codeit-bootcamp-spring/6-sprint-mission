package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException(String email) {
        super(
                ErrorCode.DUPLICATE_USER,
                "해당 이메일을 가진 사용자가 이미 존재합니다. email=" + email,
                Map.of("email", email)
        );
    }

    public static UserAlreadyExistsException byUsername(String username) {
        return new UserAlreadyExistsException (
                ErrorCode.DUPLICATE_USER,
                "해당 닉네임을 가진 사용자가 이미 존재합니다. username=" + username,
                Map.of("username", username)
        );
    }

    private UserAlreadyExistsException(ErrorCode code, String message, Map<String, Object> details) {
        super(code, message, details);
    }
}
