package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import java.util.Map;

public class AlReadyExistUserException extends UserException {

  public AlReadyExistUserException(Map<String, Object> details) {
    super(ErrorCode.ALREADY_EXISTING_USER, details);
  }
}
