package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.jwt.JwtException;
import com.sprint.mission.discodeit.security.jwt.RefreshTokenCookieProvider;
import com.sprint.mission.discodeit.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

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
    public ResponseEntity<JwtDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie refreshTokenCookie = refreshTokenCookieProvider.getRefreshTokenCookie(request);
        if (refreshTokenCookie == null) {
            throw new JwtException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        JwtDto jwtDto = authService.refreshAccessToken(refreshTokenCookie.getValue());

        refreshTokenCookieProvider.setNewRefreshTokenCookie(response, jwtDto.refreshToken());

        return ResponseEntity.ok(jwtDto);
    }
}
