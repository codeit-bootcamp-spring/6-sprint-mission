package com.sprint.mission.discodeit.exception;

public enum ErrorCode {

  NO_SUCH_USER("No such user."),
  NO_SUCH_CHANNEL("No such channel."),
  NO_SUCH_MESSAGE("No such message."),
  NO_SUCH_BINARY_CONTENT("No such binary content."),
  NO_SUCH_DATA_BASE_RECORD("No such database record."),
  NO_SUCH_READ_STATUS("No such read status."),
  NO_SUCH_USER_STATUS("No such user status."),
  ALREADY_EXISTING_USER("User already exists."),
  ALREADY_EXISTING_CHANNEL("Channel already exists."),
  ALREADY_EXISTING_MESSAGE("Message already exists."),
  ALREADY_EXISTING_READ_STATUS("Read status already exists."),
  ALREADY_EXISTING_USER_STATUS("User status already exists."),
  PASSWORD_MISMATCH("Password mismatch."),
  INVALID_CHANNEL_DATA("Invalid channel data.");

  private String message;

  ErrorCode(String message) {
    this.message = message;
  }

}
