package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;

    @GetMapping(path = "csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.info("CSRF 토큰 발급: {}", tokenValue);
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("X-CSRF-TOKEN", tokenValue)
            .build();
    }

    @GetMapping(path = "me")
    public ResponseEntity<UserDto> me(
        @AuthenticationPrincipal(expression = "userDto") UserDto userDto) {
        log.info("인증 사용자 정보 조회");
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
    }

    @PutMapping(path = "role")
    public ResponseEntity<UserDto> changeRole(
        @Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        log.info("권한 변경 요청: {}", roleUpdateRequest);
        return ResponseEntity.ok(authService.changeRole(roleUpdateRequest));
    }
}
