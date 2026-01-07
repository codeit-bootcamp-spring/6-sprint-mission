package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatusDTO {

  @Builder
  public record CreateReadStatusCommand(UUID channelId, UUID userId, Instant lastReadTimeAt) {

  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ReadStatus {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
    private Boolean notificationEnabled;

  }

  @Builder
  public record UpdateReadStatusCommand(
      UUID id,
      Instant lastReadAt,
      Boolean newNotificationEnabled
  ) {

  }

}
