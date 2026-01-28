package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.interceptor.HttpHandshakeInterceptor;
import com.sprint.mission.discodeit.interceptor.WebSocketChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
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

    private final HttpHandshakeInterceptor handshakeInterceptor;
    private final WebSocketChannelInterceptor channelInterceptor;
    private final AsyncConfig asyncConfig;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub")
                .setHeartbeatValue(new long[]{0, 0})
                .setTaskScheduler(websocketTaskScheduler());

        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Bean("websocketTaskScheduler")
    public TaskScheduler websocketTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setThreadNamePrefix("websocket-broker-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(10);
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(handshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInterceptor,
                        new SecurityContextChannelInterceptor(),
                        authorizationChannelInterceptor())
                .taskExecutor(asyncConfig.inboundChannelExecutor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor(asyncConfig.outboundChannelExecutor());
    }

    private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
        return new AuthorizationChannelInterceptor(
                MessageMatcherDelegatingAuthorizationManager.builder()
                        .anyMessage().hasAuthority(Role.ROLE_USER.name())
                        .build()
        );
    }
}
