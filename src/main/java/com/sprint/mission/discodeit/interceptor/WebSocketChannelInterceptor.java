package com.sprint.mission.discodeit.interceptor;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.exception.token.IllegalTokenException;
import com.sprint.mission.discodeit.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final TokenUtils tokenUtils;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService discodeitUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Authorization header is invalid");
            }

            String token = authHeader.substring(7);
            if (token == null) {
                throw new IllegalArgumentException("Authorization header is invalid");
            }

            JWTClaimsSet jwtClaimsSet = jwtTokenProvider.parseToken(token);
            if (jwtClaimsSet == null) {
                throw new IllegalTokenException();
            }

            UserDetails details = discodeitUserDetailsService.loadUserByUsername(jwtClaimsSet.getSubject());
            if (details == null) {
                throw new IllegalTokenException();
            }

            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            accessor.setUser(user);
        }

        return message;
    }
}
