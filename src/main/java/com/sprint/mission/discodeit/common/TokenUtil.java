package com.sprint.mission.discodeit.common;

import jakarta.servlet.http.Cookie;

public class TokenUtil {

  public static Cookie createRefreshTokenCookie(String refreshToken) {
    Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    refreshCookie.setHttpOnly(true); // JS에서 접근 불가
    refreshCookie.setPath("/");      // 모든 경로에서 전송
    refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주
    // refreshCookie.setSecure(true); // HTTPS 적용 시 필수 해제
    return refreshCookie;
  }
}
