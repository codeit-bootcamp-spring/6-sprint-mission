package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenDTO {

  private UUID id;
  private UUID userId;
  private String accessToken;
  private String refreshToken;
  private Instant createdAt;
  private Instant updatedAt;

}
