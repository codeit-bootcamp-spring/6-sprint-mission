package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tokens")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenEntity extends BaseUpdatableEntity{

  private UUID userId;

  private String accessToken;

  private String refreshToken;

  public static TokenEntity of(UUID userId, String accessToken, String refreshToken) {
    return TokenEntity.builder()
        .userId(userId)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public void updateTokens(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

}
