package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.enums.Role;

import java.util.UUID;

public record UserRoleUpdatedEvent (
        UUID userId,
        Role oldRole,
        Role newRole
){

}
