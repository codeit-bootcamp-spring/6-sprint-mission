package com.sprint.mission.discodeit.utils;

import com.sprint.mission.discodeit.jwt.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenUtils {

    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private final JwtProperties jwtProperties;

    public Cookie getRefreshCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(Math.toIntExact(jwtProperties.getRefreshKeyExpiration()));
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie emptyRefreshCookie() {
        Cookie cookie = new Cookie(REFRESH_TOKEN, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
