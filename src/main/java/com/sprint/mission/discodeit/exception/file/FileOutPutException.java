package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;
public class FileOutPutException extends FileException {
    public FileOutPutException() {
        super(ErrorCode.FILE_OUT_PUT_FAIL);
    }
}
