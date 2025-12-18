package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.Auth.LoginRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Locale;


@Slf4j
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;
    @PostMapping("login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken){
        String token = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", token);
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }
}
