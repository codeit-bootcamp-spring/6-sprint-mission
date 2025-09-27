package com.sprint.mission.discodeit.dto.UserDto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        String password
) {
}
