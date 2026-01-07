package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie refreshTokenCookie = refreshTokenCookieProvider.getRefreshTokenCookie(request);

        if (refreshTokenCookie != null) {
            refreshTokenCookieProvider.deleteRefreshTokenCookie(response);
            jwtRegistry.deleteJwtInformationByRefreshToken(refreshTokenCookie.getValue());
        }
    }
}
