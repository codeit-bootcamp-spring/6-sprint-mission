package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public final class RefreshTokenCookieProvider {
    private final RefreshTokenCookieProperties refreshTokenCookieProperties;

    public Cookie getRefreshTokenCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(refreshTokenCookieProperties.getName()))
                .findFirst().orElse(null);
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie(refreshTokenCookieProperties.getName(), "");
        refreshTokenCookie.setMaxAge(0);
        defaultCookieSet(refreshTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    public void setNewRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie newRefreshTokenCookie = new Cookie(refreshTokenCookieProperties.getName(), refreshToken);
        newRefreshTokenCookie.setMaxAge(refreshTokenCookieProperties.getMaxAge());
        defaultCookieSet(newRefreshTokenCookie);
        response.addCookie(newRefreshTokenCookie);
    }

    private void defaultCookieSet(Cookie refreshTokenCookie) {
        refreshTokenCookie.setPath(refreshTokenCookieProperties.getPath());
        refreshTokenCookie.setHttpOnly(refreshTokenCookieProperties.isHttpOnly());
        refreshTokenCookie.setSecure(refreshTokenCookieProperties.isSecure());
    }
}
