package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

  NO_SUCH_USER("No such user.", 404),
  NO_SUCH_CHANNEL("No such channel.", 404),
  NO_SUCH_MESSAGE("No such message.", 404),
  NO_SUCH_BINARY_CONTENT("No such binary content.", 404),
  NO_SUCH_DATA_BASE_RECORD("No such database record.", 404),
  NO_SUCH_READ_STATUS("No such read status.", 404),
  NO_SUCH_USER_STATUS("No such user status.", 404),
  NO_SUCH_NOTIFICATION("No such notification.", 404),
  ALREADY_EXISTING_USER("User already exists.", 409),
  ALREADY_EXISTING_CHANNEL("Channel already exists.", 409),
  ALREADY_EXISTING_MESSAGE("Message already exists.", 409),
  ALREADY_EXISTING_READ_STATUS("Read status already exists.", 409),
  ALREADY_EXISTING_USER_STATUS("User status already exists.", 409),
  PASSWORD_MISMATCH("Password mismatch.", 401),
  INVALID_CHANNEL_DATA("Invalid channel data.", 400),
  INVALID_JWT_TOKEN("Invalid JWT token.", 401),
  FORBIDDEN_NOTIFICATION_ACCESS("Forbidden notification access.", 403);

  private String message;
  private int status;

  ErrorCode(String message, int status) {
    this.message = message;
    this.status = status;
  }

}
