package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PROTECTED)
public class JwtDTO {

  @JsonProperty("userDto")
  private UserDTO.User user;

  @JsonProperty("accessToken")
  private String accessToken;

  public static JwtDTO of(UserDTO.User user, String accessToken) {
    return JwtDTO.builder()
        .user(user)
        .accessToken(accessToken)
        .build();
  }

}
