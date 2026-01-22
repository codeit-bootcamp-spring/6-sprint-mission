package com.sprint.mission.discodeit.interceptor;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;

  @Override
  public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      return message; // 헤더 접근자 없으면 통과
    }
    if (accessor.getCommand() == null) {
      return message; // 하트비트 메시지 통과
    }

    String sessionId = accessor.getSessionId();
    MDC.put("sessionId", sessionId);
    if (accessor.getUser() != null) {
      MDC.put("userId", accessor.getUser().getName());
    }

    if (accessor.getCommand() == StompCommand.CONNECT) {
      return handleConnect(accessor, message);
    }

    return message;
  }

  private @Nullable Message<?> handleConnect(StompHeaderAccessor accessor, Message<?> message) {

    String authHeader = accessor.getFirstNativeHeader("Authorization");
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {

      String token = authHeader.substring(7);

      if (jwtTokenProvider.validateToken(token)) {
        JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
        String username = claims.getSubject();

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        accessor.setUser(authentication);

        log.debug("STOMP 연결 인증 성공: 사용자 = {}, 세션id = {}",
            username, accessor.getSessionId());

        log.debug("사용자 권한: {}", authentication.getAuthorities());

        return message;
      }
    }
    throw new MessageDeliveryException("인증 토큰이 유효하지 않습니다.");
  }

  @Override
  public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
    // postSend와 달리 예외가 발생해도 호출됨
    MDC.clear();
  }
}
