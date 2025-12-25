package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;


@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @Operation(summary = "현재 사용자 정보 조회")
    ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    );

    ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);
} 