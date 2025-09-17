package com.sprint.mission.discodeit.dto.UserDto;

import java.util.UUID;

public record UserStatusCreateDto(
        UUID userId,
        Boolean status
) {
}
