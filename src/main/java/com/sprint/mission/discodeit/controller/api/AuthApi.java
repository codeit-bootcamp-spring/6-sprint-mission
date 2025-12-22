package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @Operation(summary = "인증 사용자 정보 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 반환",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    ResponseEntity<UserDto> me(UserDto userDto);

    @Operation(summary = "권한 변경")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "권한 변경 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    ResponseEntity<UserDto> changeRole(RoleUpdateRequest roleUpdateRequest);
} 
