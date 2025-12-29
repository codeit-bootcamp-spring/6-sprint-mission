package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tokens")
@Builder
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

}
