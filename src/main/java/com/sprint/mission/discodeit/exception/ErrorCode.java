package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

  USER_NOT_FOUND(404, "US001", "유저를 찾을 수 없습니다"),
  DUPLICATE_USER(409, "US002", "이미 존재하는 유저입니다."),

  CHANNEL_NOT_FOUND(404, "CH001", "채널을 찾을 수 없습니다."),
  DUPLICATE_CHANNEL(409, "CH002", "이미 존재하는 채널입니다."),
  PRIVATE_CHANNEL_UPDATE(409, "PC003", "비공개 채널은 업데이트할 수 없는 채널입니다."),

  MESSAGE_NOT_FOUND(404, "MS001", "메시지를 찾을 수 없습니다."),
  READ_STATUS_NOT_FOUND(404, "RS001", "읽기상태를 찾을 수 없습니다."),
  USER_STATUS_NOT_FOUND(404, "US101", "유저상태를 찾을 수 없습니다."),
  BINARY_CONTENT_NOT_FOUND(404, "BC001", "바이너리 데이터를 찾을 수 없습니다."),

  // Common
  UNAUTHORIZED_ACCESS(403, "CM001", "권한이 없습니다."),
  INVALID_REQUEST(400, "CM002", "잘못된 요청입니다."),
  INTERNAL_SERVER_ERROR(500, "CM003", "서버 내부 오류입니다.");

  private final int status;
  private final String code;
  private final String message;

  ErrorCode(int status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }
}
//
