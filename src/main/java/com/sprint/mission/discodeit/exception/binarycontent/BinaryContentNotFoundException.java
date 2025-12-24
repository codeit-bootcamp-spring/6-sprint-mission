package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BinaryContentNotFoundException extends BinaryContentException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BinaryContentNotFoundException(UUID binaryContentId) {
        super(
                ErrorCode.BINARY_CONTENT_NOT_FOUND,
                "BinaryContent를 찾을 수 없습니다. binaryContentId=" + binaryContentId,
                Map.of("binaryContentId", binaryContentId)
        );
    }
}
