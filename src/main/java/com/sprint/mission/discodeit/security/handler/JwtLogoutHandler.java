package com.sprint.mission.discodeit.security.handler;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.TokenUtil;
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

        if (refreshToken != null) {
            try {
                // 토큰 무효화 - 토큰에서 username 추출 -> User 조회 -> DB 삭제
                String username = jwtTokenProvider.getClaims(refreshToken).getSubject();
                userRepository.findByUsername(username)
                        .ifPresent(user -> jwtRegistry.invalidateJwtInformationByUserId(user.getId()));
            } catch (Exception e) {
                // 무시
            }
        }

        response.addCookie(TokenUtil.emptyRefreshTokenCookie());
    }
}

