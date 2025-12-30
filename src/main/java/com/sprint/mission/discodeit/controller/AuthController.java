package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.security.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.TokenUtil;
import com.sprint.mission.discodeit.security.principal.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken, HttpServletResponse response) {

        JwtInformation newInfo = authService.refreshToken(refreshToken);

        Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(newInfo.getRefreshToken());
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(
                JwtDto.builder()
                .accessToken(newInfo.getAccessToken())
                .dto(newInfo.getDto())
                .build()
        );
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String token = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(csrfToken.getHeaderName(), token)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UserResponseDto userDto = UserResponseDto.builder()
                .username(userDetails.getUsername())
                .email(userDetails.getUserResponseDto().email())
                .profile(userDetails.getUserResponseDto().profile())
                .online(userDetails.getUserResponseDto().online())
                .build();

        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/role")
    public ResponseEntity<UserResponseDto> updateUserRole(@Validated @RequestBody UserRoleUpdateRequest request) {
        UserResponseDto response = userService.updateUserRole(request);
        return ResponseEntity.ok(response);
    }

}