package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;
public class FileDownloadException extends FileException {
    public FileDownloadException() {
        super(ErrorCode.FILE_DOWNLOAD_FAIL);
    }
}