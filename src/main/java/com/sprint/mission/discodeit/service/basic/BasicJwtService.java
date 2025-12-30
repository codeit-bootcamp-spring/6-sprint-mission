package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.JwtProperties;
import com.sprint.mission.discodeit.service.JwtService;
import com.sprint.mission.discodeit.utils.TokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicJwtService implements JwtService {

  private final JwtProperties jwtProperties;
  private final TokenUtil tokenUtil;

  @Override
  public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

    tokenUtil.setHttpOnlyCookie("refreshToken", refreshToken, response,
        (int) jwtProperties.getRefreshExpiration());

  }
}
