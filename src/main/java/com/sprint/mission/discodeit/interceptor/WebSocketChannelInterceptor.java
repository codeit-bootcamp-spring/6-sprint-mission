package com.sprint.mission.discodeit.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * STOMP 메시지가 서버에 들어오거나 나갈 때 보안 검증
 */
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            if (command == null) {
                return message; // 하트비트 메시지는 통과
            }

            try {
                switch (command) {
                    case CONNECT:
                        return handleConnect(accessor, message);
                    case SUBSCRIBE:
                        return handleSubscribe(accessor, message);
                    case SEND:
                        return handleSend(accessor, message);
                    case DISCONNECT:
                        handleDisconnect(accessor);
                        break;
                    default:
                        return message;
                }
            } catch (SecurityException e) {
                System.err.println("보안 위반 - 메시지 차단: " + e.getMessage());
                return null;
            }
        }

        return message;
    }

    /**
     * 이하는 헬퍼 메서드들임.
     */
    private Message<?> handleConnect(StompHeaderAccessor accessor, Message<?> message) {
        String username = (String) accessor.getSessionAttributes().get("username");
        if (username == null) {
            throw new SecurityException("인증되지 않은 연결 시도");
        }
        return message;
    }

    private Message<?> handleSubscribe(StompHeaderAccessor accessor, Message<?> message) {
        String destination = accessor.getDestination();
        if (destination != null && destination.startsWith("/pub")) {
            String roomId = extractChannelId(destination);
            if (roomId == null || roomId.trim().isEmpty()) {
                throw new SecurityException("잘못된 구독 경로");
            }
        }
        return message;
    }

    private Message<?> handleSend(StompHeaderAccessor accessor, Message<?> message) {
        String destination = accessor.getDestination();
        String username = (String) accessor.getSessionAttributes().get("username");

        if (destination == null) {
            return message; // 하트비트 메시지
        }

        if (destination.startsWith("/pub")) {
            String roomId = extractChannelId(destination);
            if (roomId == null || username == null) {
                throw new SecurityException("잘못된 메시지 대상");
            }
        }
        return message;
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        String username = (String) accessor.getSessionAttributes().get("username");
        System.out.println("STOMP 연결 해제: " + username);
    }

    private String extractChannelId(String destination) {
        // destination: /topic/chat/10
        if (destination == null) return null;
        String[] parts = destination.split("/");
        return parts.length >= 4 ? parts[3] : null;
    }

}
