package com.sprint.mission.discodeit.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

  @Override
  public void handleUncaughtException(Throwable ex, Method method, Object... params) {

    if (ex instanceof JsonProcessingException) {
      log.error("[Async Error] JSON 처리 중 에러 발생");
    }

    log.error("[Async Error] 메서드명: {}", method.getName());
    log.error("[Async Error] 에러 메시지: {}", ex.getMessage());
    for (Object param : params) {
      log.error("[Async Error] 파라미터: {}", param);
    }
    log.error("[Async Error] StackTrace: ", ex);
  }
}
