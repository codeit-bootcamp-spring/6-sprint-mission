package com.sprint.mission.discodeit.service;

import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {

  public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken);

}
