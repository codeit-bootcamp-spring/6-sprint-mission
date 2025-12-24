package com.sprint.mission.discodeit.mapper.api;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.api.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO;
import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO.FindUserResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthApiMapper {

  public FindUserResponse toFindUserResponse(UserDTO.User user) {

    return UserResponseDTO.FindUserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .profile(user.getProfileId() != null ?
            BinaryContentResponseDTO.ReadBinaryContentResponse.builder()
                .id(user.getProfileId().getId())
                .fileName(user.getProfileId().getFileName())
                .contentType(user.getProfileId().getContentType())
                .build() :
            null)
        .role(user.getRole())
        .isOnline(user.getIsOnline())
        .build();

  }

}
