package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import java.util.Map;

public class InvalidJwtTokenException extends UserException {

  public InvalidJwtTokenException() {
    super(ErrorCode.INVALID_JWT_TOKEN, Map.of());
  }

  public InvalidJwtTokenException(
      Map<String, Object> details) {
    super(ErrorCode.INVALID_JWT_TOKEN, details);
  }
}
