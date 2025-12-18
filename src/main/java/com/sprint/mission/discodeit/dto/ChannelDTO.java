package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.enums.ChannelType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelDTO {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Channel {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private ChannelType type;
    private String name;
    private String description;
    private List<UserDTO.User> participants = new ArrayList<>();
    private Instant lastMessageAt;

    public void addParticipants(List<UserDTO.User> participants) {

      if (participants == null || participants.isEmpty()) {
        return;
      } else if (this.participants == null) {
        this.participants = new ArrayList<>();
      }

      this.participants.addAll(participants);

    }

  }

  @Builder
  public record CreatePublicChannelCommand(String name, String description) {

  }

  @Builder
  public record CreatePrivateChannelCommand(ChannelType type, List<UUID> participants,
                                            String description) {

  }

  //channel update를 위한 Request DTO
  @Builder
  public record UpdateChannelCommand(
      UUID id,
      String name,
      ChannelType type,
      List<UUID> participants,
      String description) {

  }

}
