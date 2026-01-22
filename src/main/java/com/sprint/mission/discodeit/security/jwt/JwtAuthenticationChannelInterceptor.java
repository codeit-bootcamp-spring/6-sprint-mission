package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);
    if (accessor == null || accessor.getCommand() == null) {
      return message;
    }

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = resolveToken(accessor);
      if (!StringUtils.hasText(token)) {
        throw new AuthenticationCredentialsNotFoundException("Missing Authorization token");
      }

      if (!tokenProvider.validateAccessToken(token)
          || !jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        throw new BadCredentialsException("Invalid JWT token");
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
      log.debug("Set websocket authentication for user: {}", username);
    }

    return message;
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    String bearerToken = accessor.getFirstNativeHeader("Authorization");
    if (!StringUtils.hasText(bearerToken)) {
      bearerToken = accessor.getFirstNativeHeader("authorization");
    }
    if (StringUtils.hasText(bearerToken)) {
      if (bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
      }
      if (bearerToken.startsWith("Bearer")) {
        return bearerToken.substring(6).trim();
      }
    }
    return null;
  }
}
