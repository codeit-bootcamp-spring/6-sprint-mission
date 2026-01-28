package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.common.Role;
import com.sprint.mission.discodeit.interceptor.WebSocketChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  // 구독 권한 있는지 검증 인터셉터, 그 외 통신 시 작동
  private final WebSocketChannelInterceptor channelInterceptor;

  private final MdcSecurityContextTaskDecorator decorator;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/sub")
        .setHeartbeatValue(new long[]{4000, 4000})
        .setTaskScheduler(websocketTaskScheduler());

    config.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration
        .interceptors(
            channelInterceptor,
            new SecurityContextChannelInterceptor(),
            authorizationChannelInterceptor()
        )
        .taskExecutor(inboundChannelExecutor());
  }

  @Override
  public void configureClientOutboundChannel(ChannelRegistration registration) {
    registration.taskExecutor(outboundChannelExecutor());
  }

  @Bean("websocketTaskScheduler")
  public TaskScheduler websocketTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1);
    scheduler.setThreadNamePrefix("ws-heartbeat-thread-");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(10);
    scheduler.initialize();
    return scheduler;
  }

  @Bean("websocketInboundExecutor")
  public ThreadPoolTaskExecutor inboundChannelExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8);
    executor.setMaxPoolSize(16);
    executor.setQueueCapacity(1000);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(10);
    executor.setTaskDecorator(decorator);
    executor.setThreadNamePrefix("inbound-");
    executor.initialize();
    return executor;
  }

  @Bean("websocketOutboundExecutor")
  public ThreadPoolTaskExecutor outboundChannelExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8);
    executor.setMaxPoolSize(16);
    executor.setQueueCapacity(1000);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(10);
    executor.setTaskDecorator(decorator);
    executor.setThreadNamePrefix("outbound-");
    executor.initialize();
    return executor;
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .anyMessage().hasAnyRole(
                Role.USER.name(),
                Role.ADMIN.name(),
                Role.CHANNEL_MANAGER.name()
            )
            .build()
    );
  }
}
