package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;

public class TokenUtil {

    private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 14;

    public static Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true); // JS에서 접근 불가
        refreshCookie.setPath("/");      // 모든 경로에서 전송
        refreshCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE); // 2주
        // refreshCookie.setSecure(true); // HTTPS 적용 시 필수 해제
        return refreshCookie;
    }

    public static Cookie emptyRefreshTokenCookie() {
        Cookie cookie = new Cookie("REFRESH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 삭제
        // cookie.setSecure(true); // HTTPS 환경인 경우 설정 필요
        return cookie;
    }
}
