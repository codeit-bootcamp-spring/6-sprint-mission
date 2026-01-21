package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public TaskDecorator contextCopyingTaskDecorator() {
    return runnable -> {
      Map<String, String> mdcContext = MDC.getCopyOfContextMap();
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      return () -> {
        try {
          if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
          } else {
            MDC.clear();
          }

          SecurityContext asyncContext = SecurityContextHolder.createEmptyContext();
          asyncContext.setAuthentication(authentication);
          SecurityContextHolder.setContext(asyncContext);

          runnable.run();
        } finally {
          MDC.clear();
          SecurityContextHolder.clearContext();
        }
      };
    };
  }

  @Bean(name = "taskExecutor")
  public TaskExecutor taskExecutor(TaskDecorator contextCopyingTaskDecorator) {
    int corePoolSize = Math.max(2, Runtime.getRuntime().availableProcessors());
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(corePoolSize * 2);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("async-");
    executor.setTaskDecorator(contextCopyingTaskDecorator);
    executor.initialize();
    return executor;
  }
}
