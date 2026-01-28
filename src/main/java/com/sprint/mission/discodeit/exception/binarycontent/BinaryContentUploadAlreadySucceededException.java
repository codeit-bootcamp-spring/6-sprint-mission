package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.enums.ErrorCode.BINARY_CONTENT_UPLOAD_ALREADY_SUCCEEDED;

@ResponseStatus(HttpStatus.CONFLICT)
public class BinaryContentUploadAlreadySucceededException extends DiscodeitException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BinaryContentUploadAlreadySucceededException(UUID binaryContentId) {
        super(
                BINARY_CONTENT_UPLOAD_ALREADY_SUCCEEDED,
                "이미 저장에 성공한 BinaryContent입니다. binaryContentId=" + binaryContentId,
                Map.of("binaryContentId", binaryContentId)
        );
    }
}
