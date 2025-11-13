package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserStatusCreateDto(
        UUID userId,
        Boolean status
) {
}
