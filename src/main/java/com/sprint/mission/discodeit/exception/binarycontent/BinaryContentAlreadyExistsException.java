package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class BinaryContentAlreadyExistsException extends BinaryContentException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BinaryContentAlreadyExistsException(UUID binaryContentId) {
        super(
                ErrorCode.DUPLICATE_BINARY_CONTENT,
                "해당 정보를 가진 BinaryContent가 이미 존재합니다. binaryContentId=" + binaryContentId,
                Map.of("binaryContentId", binaryContentId)
        );
    }
}
