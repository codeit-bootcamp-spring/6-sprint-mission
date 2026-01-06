package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;

public class TokenUtil {

    public static Cookie createRefreshTokenCookie(String refreshToken){
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(3600);
        return refreshCookie;
    }

    public static Cookie emptyRefreshTokenCookie(){
        Cookie cookie = new Cookie("REFRESH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
