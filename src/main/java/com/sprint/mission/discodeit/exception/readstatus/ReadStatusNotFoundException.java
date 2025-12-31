package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReadStatusNotFoundException extends ReadStatusException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ReadStatusNotFoundException(UUID readStatusId) {
        super(
                ErrorCode.READ_STATUS_NOT_FOUND,
                "ReadStatus를 찾을 수 없습니다. readStatusId=" + readStatusId,
                Map.of("readStatusId", readStatusId)
        );
    }
}
