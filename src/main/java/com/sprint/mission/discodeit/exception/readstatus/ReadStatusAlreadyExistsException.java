package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReadStatusAlreadyExistsException extends ReadStatusException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ReadStatusAlreadyExistsException(UUID userId, UUID channelId) {
        super(
                ErrorCode.DUPLICATE_READ_STATUS,
                "해당 채널과 사용자에 대해 ReadStatus가 이미 존재합니다. userId=" + userId + ", channelId=" + channelId,
                Map.of("userId", userId,  "channelId", channelId)
        );
    }
}
