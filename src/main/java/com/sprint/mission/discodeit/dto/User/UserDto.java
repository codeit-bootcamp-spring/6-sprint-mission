package com.sprint.mission.discodeit.dto.User;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.security.Role;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record UserDto(
        UUID id,
        String username,
        String email,
        BinaryContentDto profile,
        Role role,
        boolean online
) {
}
