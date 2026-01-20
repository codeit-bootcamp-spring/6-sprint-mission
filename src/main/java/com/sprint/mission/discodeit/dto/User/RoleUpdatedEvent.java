package com.sprint.mission.discodeit.dto.User;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RoleUpdatedEvent(
        UUID userId,
        String newRole,
        String oldRole
) {
}
