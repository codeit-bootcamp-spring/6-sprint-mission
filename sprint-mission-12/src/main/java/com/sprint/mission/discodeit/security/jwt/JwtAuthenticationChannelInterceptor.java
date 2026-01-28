package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null) {
      return message;
    }

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = resolveToken(accessor);
      if (!StringUtils.hasText(token)) {
        throw new IllegalArgumentException("Missing Authorization token");
      }

      if (!tokenProvider.validateAccessToken(token)
          || !jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        throw new IllegalArgumentException("Invalid JWT token");
      }

      String username = tokenProvider.getUsernameFromToken(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );

      accessor.setUser(authentication);
    }

    return message;
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    String bearerToken = accessor.getFirstNativeHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
