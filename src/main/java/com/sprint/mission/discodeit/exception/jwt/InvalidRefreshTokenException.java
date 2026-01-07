package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Collections;
import java.util.Map;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidRefreshTokenException extends JwtException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidRefreshTokenException() {
        super(
                ErrorCode.INVALID_AUTH,
                "잘못된 인증 정보입니다. 다시 로그인 해주세요",
                Collections.emptyMap()
        );
    }
}
