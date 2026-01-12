package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

  private final MdcSecurityContextTaskDecorator decorator;

  @Bean(name = "notificationEventTaskExecutor")
  public ThreadPoolTaskExecutor notificationEventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setTaskDecorator(decorator);
    executor.setThreadNamePrefix("Notification-");
    executor.initialize();
    return executor;
  }

  @Bean(name = "binaryContentEventTaskExecutor")
  public ThreadPoolTaskExecutor binaryContentEventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setTaskDecorator(decorator);
    executor.setThreadNamePrefix("BinaryContent-");
    executor.initialize();
    return executor;
  }
}
