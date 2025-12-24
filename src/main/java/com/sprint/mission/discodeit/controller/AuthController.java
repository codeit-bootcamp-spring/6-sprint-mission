package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.userDetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        log.info("CsrfToken: {}", csrfToken);
        String tokenValue = csrfToken.getToken();
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UserDto authenticatedUserDto = userDetails.getUserDto();
        return ResponseEntity.ok(authenticatedUserDto);
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(
            @RequestBody RoleUpdateRequest request
    ) {
        UserDto updatedUserDto = authService.updateRole(request);
        return ResponseEntity.ok(updatedUserDto);
    }
}
