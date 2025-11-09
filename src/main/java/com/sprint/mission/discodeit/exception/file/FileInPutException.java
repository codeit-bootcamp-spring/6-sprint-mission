package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileInPutException extends FileException{

    public FileInPutException() {
        super(ErrorCode.FILE_IN_PUT_FAIL);
    }
}