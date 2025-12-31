package com.sprint.mission.discodeit.dto.api.response;

import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO.FindUserResponse;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelResponseDTO {

  @Builder
  public record FindChannelResponse(
      UUID id,
      ChannelType type,
      String name,
      String description,
      List<FindUserResponse> participants,
      Instant lastMessageAt
  ) {

  }
}
