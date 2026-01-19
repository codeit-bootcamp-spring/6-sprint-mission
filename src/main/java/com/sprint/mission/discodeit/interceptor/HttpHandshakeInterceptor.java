package com.sprint.mission.discodeit.interceptor;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * WebSocket 연결 시점에 JWT 토큰을 검증 (미션12)
 */
@Component
@RequiredArgsConstructor
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String token = extractTokenFromQuery(request.getURI());

        if (token == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            if (!jwtTokenProvider.validateToken(token)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().add("X-Auth-Error", "TOKEN_INVALID");
                return false;
            }
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("X-Auth-Error", "TOKEN_ERROR");
            return false;
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        attributes.put("username", username);
        attributes.put("token", token);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

    }

    private String extractTokenFromQuery(URI uri) {
        // http://abc.com?token=1234
        String query = uri.getQuery(); // 쿼리스트링
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    try {
                        // URI의 ? 이후부터 추출
                        return URLDecoder.decode(param.substring(6), StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        return param.substring(6);
                    }
                }
            }
        }
        return null;
    }
}
