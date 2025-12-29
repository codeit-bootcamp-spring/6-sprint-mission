package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import org.hibernate.validator.constraints.UUID;

@Builder
public class TokenDTO {

  private UUID id;
  private UUID userId;
  private String accessToken;
  private String refreshToken;
  private Instant createdAt;
  private Instant updatedAt;

}
