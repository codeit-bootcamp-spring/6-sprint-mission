package com.sprint.mission.discodeit.mapper.api;

import com.sprint.mission.discodeit.dto.ChannelDTO;
import com.sprint.mission.discodeit.dto.api.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.dto.api.response.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.api.response.ChannelResponseDTO.FindChannelResponse;
import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class ChannelApiMapper {

  public FindChannelResponse toFindChannelResponse(ChannelDTO.Channel channel) {
    return ChannelResponseDTO.FindChannelResponse.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .participants(channel.getParticipants() != null ? channel.getParticipants().stream()
            .map(user -> UserResponseDTO.FindUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(user.getProfileId() != null
                    ? BinaryContentResponseDTO.ReadBinaryContentResponse.builder()
                    .id(user.getProfileId().getId())
                    .fileName(user.getProfileId().getFileName())
                    .size(user.getProfileId().getSize())
                    .contentType(user.getProfileId().getContentType())
                    .build() : null)
                .isOnline(user.getIsOnline())
                .build()).toList() : new ArrayList<>())
        .lastMessageAt(channel.getLastMessageAt() != null ? channel.getLastMessageAt() : null)
        .build();
  }

}
