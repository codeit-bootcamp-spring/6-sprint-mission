package com.sprint.mission.discodeit.security.handler;

import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import com.sprint.mission.discodeit.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "security", name = "login", havingValue = "jwt")
public class JwtLogoutHandler implements LogoutHandler {

  private final UserRepository userRepository;
  private final JwtRegistry jwtRegistry;
  private final TokenUtil tokenUtil;

  @Transactional
  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    String username = authentication.getName();

    UUID userId = userRepository.findByUsername(username)
        .orElseThrow(NoSuchUserException::new)
        .getId();

    jwtRegistry.invalidateJwtInformationByUserId(userId);
    tokenUtil.deleteCookie("refreshToken", response);

  }
}
