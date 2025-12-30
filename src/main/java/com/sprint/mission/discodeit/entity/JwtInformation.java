package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder(access =  AccessLevel.PROTECTED)
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtInformation {

  private UserDTO.User user;
  private String accessToken;
  private String refreshToken;

  public static JwtInformation of(UserDTO.User user, String accessToken, String refreshToken) {
    return JwtInformation.builder()
        .user(user)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public void rotate(String newAccessToken, String newRefreshToken) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
  }


}
