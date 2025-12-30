package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(DiscodeitException ex){
        ErrorResponse errorResponse = ErrorResponse.of(ex.getTimestamp(),ex.getErrorCode(),ex.getDetails());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorResponse response = ErrorResponse.of(
                Instant.now(),
                ErrorCode.UNAUTHENTICATED_USER,
                Map.of("errMessage",ex.getMessage())
        );
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse response = ErrorResponse.of(
                Instant.now(),
                ErrorCode.UNAUTHORIZED_USER,
                Map.of("errMessage",ex.getMessage())
        );
        return ResponseEntity.status(response.status()).body(response);
    }

}
