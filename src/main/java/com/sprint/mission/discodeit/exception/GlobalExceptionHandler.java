package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentAlreadyExistsException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentListNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageListNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

// 전역 예외처리 구성
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 존재하지 않는 리소스 예외처리
    @ExceptionHandler({
            UserNotFoundException.class,
            ChannelNotFoundException.class,
            MessageNotFoundException.class,
            MessageListNotFoundException.class,
            UserStatusNotFoundException.class,
            ReadStatusNotFoundException.class,
            BinaryContentNotFoundException.class,
            BinaryContentListNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(DiscodeitException e) {
        log.error(e.getMessage());
        ErrorResponse error = ErrorResponse.of(
                e.errorCode.toString(),
                e.getMessage(),
                e.details,
                e.getClass().getSimpleName(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 비공개 채널 수정 시도 예외처리
    @ExceptionHandler(PrivateChannelUpdateException.class)
    public ResponseEntity<ErrorResponse> handlePrivateChannelUpdate(PrivateChannelUpdateException e) {
        log.error(e.getMessage());
        ErrorResponse error = ErrorResponse.of(
                e.errorCode.toString(),
                e.getMessage(),
                e.details,
                e.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // 중복
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            BinaryContentAlreadyExistsException.class,
            UserStatusAlreadyExistsException.class,
            ReadStatusAlreadyExistsException.class,
    })
    public ResponseEntity<ErrorResponse> handleAlreadyExists(DiscodeitException e) {
        log.error(e.getMessage());
        ErrorResponse error = ErrorResponse.of(
                e.errorCode.toString(),
                e.getMessage(),
                e.details,
                e.getClass().getSimpleName(),
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 검증 실패 (@Valid, @NotNull, @Size 등)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        log.error(e.getMessage());

        // 이 블록은 AI가 생성
        Map<String, Object> details = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,   // 에러가 발생한 필드명
                        FieldError::getDefaultMessage, // 설정한 메시지
                        (oldValue, newValue) -> newValue // 동일 필드에 여러 에러 발생시 나중 에러로 덮어씀
                ));

        ErrorResponse error = ErrorResponse.of(
                ErrorCode.VALIDATION_FAILED.toString(),
                e.getMessage(),
                details,
                e.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // 위에서 처리하지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getMessage());

        Map<String, Object> details = Map.of(
                "exception", e.getClass().getSimpleName(),
                "message", e.getMessage()
        );

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "서버 내부 오류가 발생했습니다.",
                null, // TODO 개선?
                e.getClass().getSimpleName(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
                );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
