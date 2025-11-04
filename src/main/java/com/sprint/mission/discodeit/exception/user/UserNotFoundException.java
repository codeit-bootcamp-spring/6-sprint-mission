package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(Map<String, Object> details) {
    super(ErrorCode.USER_NOT_FOUND, details);
  }
}
