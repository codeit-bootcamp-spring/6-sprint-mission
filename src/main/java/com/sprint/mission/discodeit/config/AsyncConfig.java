package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean
  public SecurityTaskDecorator securityTaskDecorator() {
    return new SecurityTaskDecorator();
  }

  @Bean("taskExecutor")
  public TaskExecutor taskExecutor() {

    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(5);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setQueueCapacity(25);
    taskExecutor.initialize();

    taskExecutor.setTaskDecorator(securityTaskDecorator());

    return taskExecutor;

  }

}
