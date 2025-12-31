package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("권한 거부 예외 발생: {}", ex.getMessage());

        // 미션에서 요구한 403 응답 반환
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        Instant.now(),
                        "ACCESS_DENIED",
                        "해당 요청에 대한 접근 권한이 없습니다.",
                        null,
                        ex.getClass().getSimpleName(),
                        HttpStatus.FORBIDDEN.value()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("요청 유효성 검사 실패: {}", ex.getMessage());

        Map<String, Object> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        // 첫 번째 에러 메시지를 대표 메시지로 사용
        String firstErrorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                "VALIDATION_ERROR",
                firstErrorMessage,
                validationErrors,
                ex.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
        log.error("커스텀 예외 발생: code={}, message={}", exception.getErrorCode(), exception.getMessage());
        HttpStatus status = determineHttpStatus(exception);
        ErrorResponse response = new ErrorResponse(exception, status.value());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 서버 오류 발생: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private HttpStatus determineHttpStatus(DiscodeitException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return switch (errorCode) {
            case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND, BINARY_CONTENT_NOT_FOUND,
                 READ_STATUS_NOT_FOUND, USER_STATUS_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_USER, DUPLICATE_READ_STATUS, DUPLICATE_USER_STATUS -> HttpStatus.CONFLICT;
            case INVALID_USER_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
            case PRIVATE_CHANNEL_UPDATE, INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
