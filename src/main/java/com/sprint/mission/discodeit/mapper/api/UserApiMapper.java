package com.sprint.mission.discodeit.mapper.api;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.UserStatusDTO;
import com.sprint.mission.discodeit.dto.api.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO;
import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO.CheckUserOnlineResponse;
import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO.FindUserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserApiMapper {

  public FindUserResponse toFindUserResponse(UserDTO.User user) {
    return UserResponseDTO.FindUserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .profile(user.getProfileId() != null ?
            BinaryContentResponseDTO.ReadBinaryContentResponse.builder()
                .id(user.getProfileId().getId())
                .fileName(user.getProfileId().getFileName())
                .size(user.getProfileId().getSize())
                .contentType(user.getProfileId().getContentType())
                .build() :
            null)
        .isOnline(user.getIsOnline())
        .build();
  }

  public CheckUserOnlineResponse userStatusToCheckUserOnlineResponse(
      UserStatusDTO.UserStatus userStatus) {
    return UserResponseDTO.CheckUserOnlineResponse.builder()
        .id(userStatus.getId())
        .userId(userStatus.getUserId())
        .lastOnlineAt(userStatus.getLastActiveAt())
        .isOnline(true)
        .build();
  }

}
