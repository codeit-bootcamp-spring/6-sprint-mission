package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.security.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.TokenUtil;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> refresh(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        JwtInformation newInfo = authService.refreshToken(refreshToken);

        Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(newInfo.refreshToken());
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(
                JwtDto.builder()
                .accessToken(newInfo.accessToken())
                .userDto(newInfo.dto())
                .build()
        );
    }

    @PutMapping("/role")
    public ResponseEntity<UserResponseDto> updateUserRole(@Validated @RequestBody UserRoleUpdateRequest request) {
        UserResponseDto response = userService.updateUserRole(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 불필요하지만, 프론트가 csrf-token 요청을 호출하므로 응답 반환
     */
    @GetMapping("/csrf-token")
    public ResponseEntity<?> getCsrfToken() {
        return ResponseEntity.ok().build();
    }

}