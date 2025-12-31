package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends MessageException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MessageNotFoundException(UUID messageId) {
        super(
                ErrorCode.MESSAGE_NOT_FOUND,
                "메시지를 찾을 수 없습니다. messageId=" + messageId,
                Map.of("messageId", messageId)
        );
    }
}
