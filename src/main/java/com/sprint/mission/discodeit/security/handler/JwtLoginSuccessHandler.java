package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.JwtDTO;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.mapper.UserEntityMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.provider.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "security", name = "login", havingValue = "jwt")
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final UserEntityMapper userEntityMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    String username = authentication.getName();
    String role = authentication.getAuthorities().stream()
        .findFirst()
        .map(grantedAuthority -> grantedAuthority.getAuthority())
        .orElse("USER");
    UserEntity userEntity = userRepository.findByUsername(username)
        .orElseThrow(NoSuchUserException::new);

    String accessToken = jwtTokenProvider.generateAccessToken(username, role);
    String refreshToken = jwtTokenProvider.generateRefreshToken(username, role);

    JwtDTO jwtDTO = JwtDTO.of(userEntityMapper.toUser(userEntity), accessToken);

    response.getWriter().write(objectMapper.writeValueAsString(jwtDTO));

    //set refresh token in HttpOnly cookie
    Cookie cookie = new Cookie("jwt", accessToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
    response.addCookie(cookie);

  }
}
