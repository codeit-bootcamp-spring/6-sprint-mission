package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.common.TokenUtil;
import com.sprint.mission.discodeit.dto.model.JwtDto;
import com.sprint.mission.discodeit.dto.model.JwtInformation;
import com.sprint.mission.discodeit.dto.model.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserMapper userMapper;
  private final BasicAuthService authService;

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(
      @RequestBody RoleUpdateRequest updateRequest
  ) {
    User updated = authService.updateRole(updateRequest);
    return ResponseEntity.ok(userMapper.toDto(updated));
  }

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(@CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse response) {

    if (refreshToken == null || refreshToken.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Refresh token is missing"));
    }

    JwtInformation newInfo = authService.refreshToken(refreshToken);

    if (newInfo == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired refresh token"));
    }

    Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(newInfo.getRefreshToken());
    response.addCookie(refreshCookie);

    JwtDto jwtdto = JwtDto.builder()
        .accessToken(newInfo.getAccessToken())
        .userDto(newInfo.getUserDto())
        .build();

    return ResponseEntity.ok(jwtdto);
  }
}
