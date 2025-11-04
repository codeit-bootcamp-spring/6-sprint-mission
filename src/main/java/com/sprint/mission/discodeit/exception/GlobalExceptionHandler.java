package com.sprint.mission.discodeit.exception;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleUserException(DiscodeitException ex) {
    ErrorCode errorcode = ex.getErrorCode();
    log.debug(Arrays.toString(ex.getStackTrace()));     // 자세한 스택트레이스는 debug로 처리
    return ResponseEntity
        .status(errorcode.getStatus())
        .body(ErrorResponse.of(ex, errorcode));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handelValidationException(MethodArgumentNotValidException ex) {
    ErrorCode errorcode = ErrorCode.INVALID_REQUEST;
    log.info("사용자의 잘못된 요청으로 인한 검증 실패", ex);
    return ResponseEntity
        .status(errorcode.getStatus())
        .body(ErrorResponse.validError(ex, errorcode));
  }


  // 이외 예외 처리
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    ErrorCode errorcode = ErrorCode.INTERNAL_SERVER_ERROR;
    log.error("예상하지 못한 에러", ex);
    return ResponseEntity
        .status(errorcode.getStatus())
        .body(ErrorResponse.error(ex, errorcode));
  }
}
