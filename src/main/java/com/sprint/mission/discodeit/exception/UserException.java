package com.sprint.mission.discodeit.exception;

public class UserException extends DiscodeitException {

  public UserException(ErrorCode errorCode, java.util.Map<String, Object> details) {
    super(errorCode, details);
  }
}
