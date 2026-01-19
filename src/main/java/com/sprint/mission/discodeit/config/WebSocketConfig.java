package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.interceptor.HttpHandshakeInterceptor;
import com.sprint.mission.discodeit.interceptor.WebSocketChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP 웹소켓 통신 활성화 위한 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final HttpHandshakeInterceptor handshakeInterceptor;
    private final WebSocketChannelInterceptor channelInterceptor;

    // 메세지 이동 경로 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub") // 클라이언트가 메시지 받기 위해 구독하는 경로
                .setHeartbeatValue(new long[]{10000, 10000}) // 서버와 클라이언트가 서로 10초마다 상태 확인
                .setTaskScheduler(websocketTaskScheduler()); // 헬스체크를 위한 스케줄러 등록

        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 메시지를 보낼 때 사용하는 경로
    }

    // 클라이언트가 웹소켓 처음 접속 시 쓸 연결 통로
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // CORS 허용
                .addInterceptors(handshakeInterceptor)
                .withSockJS(); // 웹소켓 못 쓰는 브라우저의 경우 다른 기술로 대체(Fallback)
    }

    // 클라이언트가 보낸 STOMP 메시지를 가공하고 컨트롤러(@MessageMapping)로 전달
    // InboundChannel: 클라이언트가 보낸 메시지가 서버 내부(컨트롤러 등)로 들어오는 통로
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInterceptor) // 인증 수행
                .taskExecutor(inboundChannelExecutor());
    }

    // 서버에서 브로커를 거쳐 클라이언트에게 메시지를 전송
    // OutboundChannel: 서버가 클라이언트에게 메시지를 내보내는 통로
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor(outboundChannelExecutor());
    }

    @Bean("websocketTaskScheduler")
    public TaskScheduler websocketTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors()); // 서버의 CPU 코어 수만큼
        scheduler.setThreadNamePrefix("websocket-broker-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(10); // 10초간 기다린 뒤 종료
        scheduler.initialize(); // 가동
        return scheduler;
    }

    @Bean("websocketInboundExecutor")
    public ThreadPoolTaskExecutor inboundChannelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
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
        executor.setThreadNamePrefix("outbound-");
        executor.initialize();
        return executor;
    }

}
