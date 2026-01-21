package com.sprint.mission.discodeit.dto.kafka;

import com.sprint.mission.discodeit.enums.Role;

import java.util.UUID;

public record UserRoleUpdatedPayload(
        UUID userId,
        Role oldRole,
        Role newRole
) {
}
