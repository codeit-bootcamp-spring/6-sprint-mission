package com.sprint.mission.discodeit.config;

//import com.sprint.mission.discodeit.decorator.ContextTaskDecorator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.TaskDecorator;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//@Configuration
//@EnableAsync
//public class AsyncConfig {
//
//    @Bean
//    public TaskDecorator contextTaskDecorator() {
//        return new ContextTaskDecorator();
//    }
//
//    @Bean(name = "eventTaskExecutor")
//    public ThreadPoolTaskExecutor  eventTaskExecutor() {
//        ThreadPoolTaskExecutor  executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(10);
//        executor.setQueueCapacity(50);
//        executor.setThreadNamePrefix("EventThread-");
//        executor.setTaskDecorator(contextTaskDecorator());
//        executor.initialize();
//        return executor;
//    }
//}