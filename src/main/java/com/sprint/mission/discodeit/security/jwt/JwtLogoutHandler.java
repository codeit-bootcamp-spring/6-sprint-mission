package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            refreshToken = Arrays.stream(cookies)
                    .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            try {
                String userName = jwtTokenProvider.getClaims(refreshToken).getSubject();
                userRepository.findByUsername(userName)
                        .ifPresent(user -> jwtRegistry.invalidateJwtInformationByUserId(user.getId()));
            } catch (Exception ignored) {}
        }

        response.addCookie(TokenUtil.emptyRefreshTokenCookie());
    }
}
