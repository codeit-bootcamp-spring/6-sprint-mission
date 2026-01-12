package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.common.Role;
import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    Role oldRole,
    Role newRole
) {

}
