package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.common.Role;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateRoleNotificationRequest(
    UUID userId,
    Role oldRole,
    Role newRole
) {

}
