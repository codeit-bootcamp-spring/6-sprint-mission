package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {

    UUID requestId = UUID.randomUUID();
    MDC.put("requestId", requestId.toString());
    MDC.put("requestMethod", request.getMethod());
    MDC.put("requestURI", request.getRequestURI());

    response.setHeader("Discodeit-Request-ID", requestId.toString());

    return true;

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception e) {
    MDC.clear();
  }


}
