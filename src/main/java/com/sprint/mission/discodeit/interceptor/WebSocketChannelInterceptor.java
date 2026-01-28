package com.sprint.mission.discodeit.interceptor;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.messaging.simp.stomp.StompCommand.*;

/**
 * STOMP 메시지가 서버에 들어오거나 나갈 때 보안 검증
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;

    private static final String TOKEN_PREFIX = "Bearer ";

    // 클라이언트는 채널 입장 시 웹소켓으로 /sub/channels.{channelId}.messages 를 구독해 메시지를 수신
    private static final Pattern SUB_CHANNEL_ID_PATTERN = Pattern.compile("^/sub/channels\\.([^.]+)\\..*");


    // 모든 STOMP 메시지에 대해 수행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) return message;

        try {
            switch (accessor.getCommand()) {
                case CONNECT:
                    authenticate(accessor);
                    return handleConnect(accessor, message);

                case SUBSCRIBE:
                    return handleSubscribe(accessor, message);

                case SEND:
                    return handleSend(accessor, message);

                case DISCONNECT:
                    handleDisconnect(accessor);
                    break;

                default:
                    break;
            }
        } catch (SecurityException e) {
            log.warn("보안 정책 위반 메시지 차단: " + e.getMessage());
            return null;
        }
        return message;
    }

    /**
     * 이하는 헬퍼 메서드들임.
     */
    private void authenticate(StompHeaderAccessor accessor) {
        if (accessor != null && CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor);
            if (StringUtils.hasText(token) && jwtTokenProvider.validateAccessToken(token) && isTokenValidInRegistry(token)){
                JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
                if (claims != null) {
                    String username = claims.getSubject();

                    // 3. 인증 객체 생성 및 accessor에 저장
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // SecurityContextHolder 대신 accessor에 설정 (웹소켓 세션 동안 유지됨)
                    accessor.setUser(authentication);

                    // 추가로 세션 속성에 정보를 저장하고 싶을 경우
                    if (accessor.getSessionAttributes() != null) {
                        accessor.getSessionAttributes().put("username", username);
                    }

                    log.debug("STOMP CONNECT 인증 성공: {}", username);
                } else {
                    log.warn("STOMP CONNECT 인증 실패: 유효하지 않은 토큰");
                    throw new SecurityException("인증되지 않은 연결 시도입니다.");
                }
            }
        }
    }

    // STOMP 헤더에서 Bearer 토큰 추출
    private String resolveToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    // JwtRegistry를 통한 상태 검증
    private boolean isTokenValidInRegistry(String token) {
        return jwtRegistry.hasActiveJwtInformationByAccessToken(token);
    }

    private Message<?> handleConnect(StompHeaderAccessor accessor, Message<?> message) {
        String username = (String) accessor.getSessionAttributes().get("username");
        if (username == null) {
            throw new SecurityException("인증되지 않은 연결 시도");
        }
        return message;
    }

    private Message<?> handleSubscribe(StompHeaderAccessor accessor, Message<?> message) {
        String destination = accessor.getDestination();
        Principal principal = accessor.getUser();
        if (principal == null) throw new SecurityException("인증 정보가 없습니다.");

        if (destination != null && destination.startsWith("/sub/channels.")) {
            Matcher matcher = SUB_CHANNEL_ID_PATTERN.matcher(destination);
            if (!matcher.find()) {
                throw new SecurityException("잘못된 구독 경로 형식입니다.");
            }
        }
        return message;
    }

    private Message<?> handleSend(StompHeaderAccessor accessor, Message<?> message) {
        String destination = accessor.getDestination();
        Principal principal = accessor.getUser();
        if (principal == null) throw new SecurityException("인증 정보가 없습니다.");

        if (destination != null && destination.startsWith("/pub/messages")) {
            log.debug("공통 메시지 엔드포인트 발행 시도 - 유저: {}", principal.getName());
            return message;
        }
        return message;
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        String username = (String) accessor.getSessionAttributes().get("username");
        log.info("STOMP 연결 해제: " + username);
    }

}
