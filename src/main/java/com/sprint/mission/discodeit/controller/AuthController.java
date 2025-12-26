package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.principal.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.auth.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    private final AuthService authService;
    private final UserService userService;

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