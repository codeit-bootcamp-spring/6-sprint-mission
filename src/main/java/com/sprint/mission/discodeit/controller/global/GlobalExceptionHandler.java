package com.sprint.mission.discodeit.controller.global;

import com.sprint.mission.discodeit.dto.api.ErrorApiDTO;
import com.sprint.mission.discodeit.exception.binarycontent.NoSuchBinaryContentException;
import com.sprint.mission.discodeit.exception.channel.AllReadyExistChannelException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelDataException;
import com.sprint.mission.discodeit.exception.channel.NoSuchChannelException;
import com.sprint.mission.discodeit.exception.message.NoSuchMessageException;
import com.sprint.mission.discodeit.exception.readstatus.AllReadyExistReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.NoSuchReadStatusException;
import com.sprint.mission.discodeit.exception.user.AllReadyExistUserException;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.exception.user.PasswordMismatchException;
import com.sprint.mission.discodeit.exception.userstatus.AllReadyExistUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.NoSuchUserStatusException;
import jakarta.validation.ValidationException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> IllegalArgumentException(
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
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> ValidationException(ValidationException e) {

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
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> MethodArgumentNotValidException(
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

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> Exception(Exception e) {

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

  @ExceptionHandler(NoSuchBinaryContentException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> NoSuchBinaryContentException(
      NoSuchBinaryContentException e) {

    log.error("NoSuchBinaryContentException occurred", e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .status(HttpStatus.NOT_FOUND.value())
            .build());

  }

  @ExceptionHandler(AllReadyExistChannelException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> AllReadyExistChannelException(
      AllReadyExistChannelException e) {

    log.error("AllReadyExistChannelException occurred", e);

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.CONFLICT.value()))
            .status(HttpStatus.CONFLICT.value())
            .build());

  }

  @ExceptionHandler(InvalidChannelDataException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> InvalidChannelDataException(
      InvalidChannelDataException e) {

    log.error("InvalidChannelDataException occurred", e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.BAD_REQUEST.value()))
            .status(HttpStatus.BAD_REQUEST.value())
            .build());

  }

  @ExceptionHandler(NoSuchChannelException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> NoSuchChannelException(
      NoSuchChannelException e) {

    log.error("NoSuchChannelException occurred", e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .status(HttpStatus.NOT_FOUND.value())
            .build());

  }

  @ExceptionHandler(NoSuchMessageException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> NoSuchMessageException(
      NoSuchMessageException e) {

    log.error("NoSuchMessageException occurred", e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .status(HttpStatus.NOT_FOUND.value())
            .build());

  }

  @ExceptionHandler(AllReadyExistReadStatusException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> AllReadyExistReadStatusException(
      AllReadyExistReadStatusException e) {

    log.error("AllReadyExistReadStatusException occurred", e);

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.CONFLICT.value()))
            .status(HttpStatus.CONFLICT.value())
            .build());

  }

  @ExceptionHandler(NoSuchReadStatusException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> NoSuchReadStatusException(
      NoSuchReadStatusException e) {

    log.error("NoSuchReadStatusException occurred", e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .status(HttpStatus.NOT_FOUND.value())
            .build());

  }

  @ExceptionHandler(AllReadyExistUserException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> AllReadyExistUserException(
      AllReadyExistUserException e) {

    log.error("AllReadyExistUserException occurred", e);

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.CONFLICT.value()))
            .status(HttpStatus.CONFLICT.value())
            .build());

  }

  @ExceptionHandler(NoSuchUserException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> NoSuchUserException(
      NoSuchUserException e) {

    log.error("NoSuchUserException occurred", e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .status(HttpStatus.NOT_FOUND.value())
            .build());

  }

  @ExceptionHandler(PasswordMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> PasswordMismatchException(
      PasswordMismatchException e) {

    log.error("PasswordMismatchException occurred", e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.BAD_REQUEST.value()))
            .status(HttpStatus.BAD_REQUEST.value())
            .build());

  }

  @ExceptionHandler(AllReadyExistUserStatusException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> AllReadyExistUserStatusException(
      AllReadyExistUserStatusException e) {

    log.error("AllReadyExistUserStatusException occurred", e);

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .exceptionType(String.valueOf(HttpStatus.CONFLICT.value()))
            .status(HttpStatus.CONFLICT.value())
            .build());

  }

  @ExceptionHandler(NoSuchUserStatusException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> NoSuchUserStatusException(
      NoSuchUserStatusException e) {

    log.error("NoSuchUserStatusException occurred", e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorApiDTO.ErrorApiResponse.builder()
            .timestamp(e.getTimestamp())
            .code(e.getErrorCode().name())
            .message(e.getMessage())
            .details(e.getDetails())
            .exceptionType(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .status(HttpStatus.NOT_FOUND.value())
            .build());

  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<ErrorApiDTO.ErrorApiResponse> handleMethodArgumentNotValidExceptionForForbidden(
      MethodArgumentNotValidException e) {

    log.error("MethodArgumentNotValidException occurred - Forbidden", e);

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorApiDTO.ErrorApiResponse.builder()
        .timestamp(Instant.now())
        .code(String.valueOf(HttpStatus.FORBIDDEN.value()))
        .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
        .exceptionType(String.valueOf(HttpStatus.FORBIDDEN.value()))
        .status(HttpStatus.FORBIDDEN.value())
        .build());

  }

}
