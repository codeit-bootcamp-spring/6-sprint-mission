package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails details) {
      jwtRegistry.invalidateJwtInformationByUserId(details.getUserDto().id());
    }
    response.addHeader("Set-Cookie", buildExpiredRefreshTokenCookie().toString());
  }

  private ResponseCookie buildExpiredRefreshTokenCookie() {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
        .httpOnly(true)
        .path("/")
        .maxAge(0)
        .build();
  }
}
