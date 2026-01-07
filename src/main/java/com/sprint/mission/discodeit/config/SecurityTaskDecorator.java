package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {

    // Capture the security context and MDC context
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Map<String, String> mdcContext = MDC.getCopyOfContextMap();

    return () -> {
      try {

        // Set the captured contexts to the new thread
        SecurityContextHolder.setContext(securityContext);
        if (mdcContext != null) {
          MDC.setContextMap(mdcContext);
        }

        runnable.run();
      } finally {
        // Clear security context or any other context if needed
        SecurityContextHolder.clearContext();
        MDC.clear();
      }
    };
  }

}
