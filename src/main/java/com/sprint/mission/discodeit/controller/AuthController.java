package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtProperties;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final JwtProperties jwtProperties;
  private final DiscodeitUserDetailsService userDetailsService;

  private static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PutMapping("role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    log.info("권한 수정 요청");
    UserDto userDto = authService.updateRole(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  @PostMapping("refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
    log.info("토큰 재발급 요청");
    String refreshToken = extractRefreshToken(request);
    if (refreshToken == null || refreshToken.isBlank()) {
      return unauthorizedResponse();
    }

    if (!jwtTokenProvider.validateToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      return unauthorizedResponse();
    }

    String username = jwtTokenProvider.getClaims(refreshToken).getSubject();
    if (username == null) {
      return unauthorizedResponse();
    }

    var userDetails = userDetailsService.loadUserByUsername(username);
    if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
      return unauthorizedResponse();
    }

    UserDto userDto = discodeitUserDetails.getUserDto();
    String accessToken = jwtTokenProvider.createAccessToken(
        userDto.username(),
        userDto.role().toString()
    );
    String newRefreshToken = jwtTokenProvider.createRefreshToken(
        userDto.username(),
        userDto.role().toString()
    );

    jwtRegistry.rotateJwtInformation(
        refreshToken,
        new JwtInformation(userDto, accessToken, newRefreshToken)
    );
    response.addHeader("Set-Cookie", buildRefreshTokenCookie(newRefreshToken).toString());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(JwtDto.of(userDto, accessToken));
  }

  private String extractRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  private ResponseCookie buildRefreshTokenCookie(String refreshToken) {
    long maxAgeSeconds = Duration.ofMillis(jwtProperties.getRefreshTokenValidityInMs()).getSeconds();
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
        .httpOnly(true)
        .path("/")
        .maxAge(maxAgeSeconds)
        .build();
  }

  private ResponseEntity<ErrorResponse> unauthorizedResponse() {
    ErrorResponse errorResponse = new ErrorResponse(
        new RuntimeException("Refresh token is invalid"),
        HttpStatus.UNAUTHORIZED.value()
    );
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse);
  }
}
