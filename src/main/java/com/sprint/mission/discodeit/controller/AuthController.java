package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.JwtDTO;
import com.sprint.mission.discodeit.dto.TokenDTO;
import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.api.request.UserRequestDTO;
import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO.FindUserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.mapper.api.AuthApiMapper;
import com.sprint.mission.discodeit.mapper.api.UserApiMapper;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.JwtService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API 컨트롤러
 */
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;
  private final JwtService jwtService;
  private final AuthApiMapper authApiMapper;
  private final UserApiMapper userApiMapper;

  /**
   * 사용자 로그인
   *
   * //@param loginRequest 로그인 요청 정보
   * //@return 로그인된 사용자 정보
   */
  /*@Operation(
      summary = "사용자 로그인",
      description = "사용자 인증을 수행하고 JWT 토큰을 발급합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "로그인 성공",
              content = @Content(schema = @Schema(implementation = FindUserResponse.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "사용자를 찾을 수 없음",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          )
      }
  )
  @PostMapping(value = "/login")
  public ResponseEntity<FindUserResponse> login(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "로그인 요청 정보",
          required = true,
          content = @Content(schema = @Schema(implementation = LoginRequest.class))
      )
      @RequestBody @Valid AuthRequestDTO.LoginRequest loginRequest) {

    log.info("Login attempt for user: {}", loginRequest.username());

    UserDTO.User user = authService.login(UserDTO.toLoginCommand(
        loginRequest.username(),
        loginRequest.password()
    ));

    return ResponseEntity.ok(authApiMapper.toFindUserResponse(user));

  }*/

  @PutMapping("/role")
  public ResponseEntity<FindUserResponse> changeUserRole(
      @Valid UserRequestDTO.UserRoleUpdateRequest request
  ) {

    log.info("Role change attempt for user ID: {}", request.userId());

    UserDTO.User updatedUser = userService.updateUserRole(
        UserDTO.UpdateUserRoleCommand.builder()
            .userId(request.userId())
            .newRole(request.newRole())
            .build()
    );

    return ResponseEntity.ok(userApiMapper.toFindUserResponse(updatedUser));
  }

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.status(HttpStatusCode.valueOf(203)).build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDTO> refreshToken(
      @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken, HttpServletResponse response
  ) {

    TokenDTO token = authService.renewAccessToken(refreshToken);

    jwtService.setRefreshTokenCookie(response, token.getRefreshToken());

    return ResponseEntity.ok(
        JwtDTO.of(userService.findUserById(token.getUserId()), token.getAccessToken())
    );
  }

  @GetMapping("/me")
  public ResponseEntity<FindUserResponse> getCurrentUser(@AuthenticationPrincipal DiscodeitUserDetails principal) {
    return ResponseEntity.ok(authApiMapper.toFindUserResponse(principal.getUser()));
  }

}
