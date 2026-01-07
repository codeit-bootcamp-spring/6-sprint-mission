package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.Auth.JwtDto;
import com.sprint.mission.discodeit.dto.Auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.TokenUtil;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @GetMapping("me")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(userDetails.getUserDto());
    }

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken){
        String token = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", token);
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request){
        UserDto userDto = authService.updateRole(request);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtDto> refresh(
            @CookieValue(value = "REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response
    ){
        JwtInformation newInfo = authService.refreshToken(refreshToken);

        Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(newInfo.getRefreshToken());
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(JwtDto.builder()
                .accessToken(newInfo.getAccessToken())
                .userDto(newInfo.getUserDto())
                .build());
    }
}
