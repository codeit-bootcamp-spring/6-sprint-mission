package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

  public static ErrorResponse of(DiscodeitException ex, ErrorCode errorCode) {
    return new ErrorResponse(
        ex.getTimestamp(),
        errorCode.getCode(),
        errorCode.getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        errorCode.getStatus()
    );
  }

  public static ErrorResponse error(Exception ex, ErrorCode errorCode) {
    return new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        null,
        ex.getClass().getSimpleName(),
        errorCode.getStatus()
    );
  }

  /*
  1. 요구사항 ->
  유효성 검증 실패 시 상세한 오류 메시지를 포함한 응답을 반환하세요.
  2. 프론트엔드 에러 표시 코드 ->
  {children:[h.jsx(xo,{children:"상세 정보:"}),h.jsx(So,{children:Object.entries(S).map(([R,C])=>h.jsxs("div",{children:[R,": ",String(C)]},R))}
   */
  public static ErrorResponse validError(MethodArgumentNotValidException ex, ErrorCode errorCode) {

    /*
    List<FieldErrorResponse> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError -> new FieldErrorResponse(
            fieldError.getField(),
            fieldError.getRejectedValue(),
            fieldError.getDefaultMessage()
        ))
        .toList();
     */

    List<String> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError -> {
              String toLog = "[필드: " + fieldError.getField() +
                  ", 입력값: " + fieldError.getRejectedValue() +
                  ", 메시지: " + fieldError.getDefaultMessage() + "]";
              log.info("검증 에러 발생: {}", toLog);

              // 유저에게는 메시지만 전달
              String toUser = fieldError.getDefaultMessage();
              return toUser;
            }
        ).toList();

    return new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        Map.of("검증 에러", fieldErrors),
        ex.getClass().getSimpleName(),
        errorCode.getStatus()
    );
  }
}
