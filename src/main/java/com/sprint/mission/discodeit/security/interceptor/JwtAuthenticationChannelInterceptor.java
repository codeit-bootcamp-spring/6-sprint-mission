package com.sprint.mission.discodeit.security.interceptor;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.exception.user.InvalidJwtTokenException;
import com.sprint.mission.discodeit.security.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      throw new IllegalArgumentException("No StompHeaderAccessor found");
    }

    String token = extractToken(accessor);

    if (token == null || !isValidToken(token)) {
      throw new InvalidJwtTokenException(new HashMap<>());
    }

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      Authentication authentication = getAuthenticationFromToken(token);

      accessor.setUser(authentication);

    }
    return message;
  }

  private Authentication getAuthenticationFromToken(String token) {

    JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
    String username = claims.getSubject();
    String role = claims.getClaim("role").toString();

    List<SimpleGrantedAuthority> authorities = new ArrayList<>(Collections.singleton(new SimpleGrantedAuthority(role)));
    UserDetails userDetails = new User(username, role, authorities);

    return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

  }

  private boolean isValidToken(String token) {
    return jwtTokenProvider.validateAccessToken(token)
        && jwtRegistry.hasActiveJwtInformationByAccessToken(token);
  }

  private String extractToken(StompHeaderAccessor accessor) {

    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }

    return null;

  }

}
