package com.sprint.mission.discodeit.dto.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.dto.api.response.BinaryContentResponseDTO.ReadBinaryContentResponse;
import com.sprint.mission.discodeit.entity.enums.Role;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseDTO {

  @Builder
  public record FindUserResponse(
      UUID id,
      String username,
      String email,
      @JsonProperty("profile")
      ReadBinaryContentResponse profile,
      Role role,
      @JsonProperty("online")
      boolean isOnline
  ) {

  }

  @Builder
  public record CheckUserOnlineResponse(
      UUID id,
      UUID userId,
      @JsonProperty("lastActiveAt")
      Instant lastOnlineAt,
      boolean isOnline
  ) {

  }
}
