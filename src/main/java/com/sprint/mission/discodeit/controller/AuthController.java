package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    @Override
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        if (userDetails == null) {
            log.debug("인증된 사용자 정보가 없습니다.");
            return ResponseEntity.status(401).build();
        }

        log.debug("현재 사용자 정보 조회 성공: {}", userDetails.getUsername());

        return ResponseEntity.ok(userDetails.getUserDto());
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 발급: {}", tokenValue);

        return ResponseEntity.noContent().build();
    }
}
