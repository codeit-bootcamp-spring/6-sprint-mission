package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(DiscodeitException ex){
        ErrorResponse errorResponse = ErrorResponse.of(ex.getTimestamp(),ex.getErrorCode(),ex.getDetails());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
    }
}
