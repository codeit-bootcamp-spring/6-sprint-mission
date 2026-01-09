
package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.TokenUtil;
import com.sprint.mission.discodeit.dto.model.JwtDto;
import com.sprint.mission.discodeit.dto.model.JwtInformation;
import com.sprint.mission.discodeit.dto.model.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    UserDto userDto = userDetails.getUserDto();

    String accessToken = jwtTokenProvider.createAccessToken(userDetails);
    String refreshToken = jwtTokenProvider.createRefreshToken(userDetails);

    // 레지스트리에 등록
    JwtInformation info = new JwtInformation(userDto, accessToken, refreshToken);
    jwtRegistry.registerJwtInformation(info);

    Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(refreshToken);
    response.addCookie(refreshCookie);

    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

    JwtDto jwtDto = JwtDto.builder()
        .accessToken(accessToken)
        .userDto(userDto)
        .build();

    objectMapper.writeValue(response.getWriter(), jwtDto);
  }
}
