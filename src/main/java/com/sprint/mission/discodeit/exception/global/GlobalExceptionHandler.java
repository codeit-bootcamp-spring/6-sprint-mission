package com.sprint.mission.discodeit.exception.global;

import com.sprint.mission.discodeit.dto.api.ErrorApiDTO;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.binarycontent.NoSuchBinaryContentException;
import com.sprint.mission.discodeit.exception.channel.AllReadyExistChannelException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelDataException;
import com.sprint.mission.discodeit.exception.channel.NoSuchChannelException;
import com.sprint.mission.discodeit.exception.message.NoSuchMessageException;
import com.sprint.mission.discodeit.exception.notification.ForbiddenNotificationAccessException;
import com.sprint.mission.discodeit.exception.notification.NoSuchNotificationException;
import com.sprint.mission.discodeit.exception.readstatus.AllReadyExistReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.NoSuchReadStatusException;
import com.sprint.mission.discodeit.exception.user.AlReadyExistUserException;
import com.sprint.mission.discodeit.exception.user.InvalidJwtTokenException;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.exception.user.PasswordMismatchException;
import com.sprint.mission.discodeit.exception.userstatus.AllReadyExistUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.NoSuchUserStatusException;
import jakarta.validation.ValidationException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> handleIllegalArgumentException(
      IllegalArgumentException e) {

    log.error("IllegalArgumentException occurred", e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorApiDTO.ErrorApiResponse.builder()
        .timestamp(Instant.now())
        .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .message(e.getMessage())
        .exceptionType(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .status(HttpStatus.BAD_REQUEST.value())
        .build());
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> handleValidationException(ValidationException e) {

    log.error("ValidationException occurred", e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorApiDTO.ErrorApiResponse.builder()
        .timestamp(Instant.now())
        .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .message(e.getMessage())
        .exceptionType(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .status(HttpStatus.BAD_REQUEST.value())
        .build());

  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {

    log.error("MethodArgumentNotValidException occurred", e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorApiDTO.ErrorApiResponse.builder()
        .timestamp(Instant.now())
        .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
        .exceptionType(String.valueOf(HttpStatus.BAD_REQUEST.value()))
        .status(HttpStatus.BAD_REQUEST.value())
        .build());

  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> handleDiscodeitException(
      DiscodeitException e) {

    log.error("DiscodeitException occurred", e);

    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.valueOf(e.getErrorCode().getStatus())))
            .status(e.getErrorCode().getStatus())
            .build());

  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> handleException(Exception e) {

    log.error("Exception occurred", e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(Instant.now())
            .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .message("Internal Server Error")
            .exceptionType(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build());
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> handleAuthorizationDeniedException(
      AuthorizationDeniedException e) {
    log.error("AuthorizationDeniedException occurred", e);
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(Instant.now())
            .code(String.valueOf(HttpStatus.FORBIDDEN.value()))
            .message("Authorization Denied: " + e.getMessage())
            .exceptionType(String.valueOf(HttpStatus.FORBIDDEN.value()))
            .status(HttpStatus.FORBIDDEN.value())
            .build());
  }

}
