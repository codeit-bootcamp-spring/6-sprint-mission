package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record UserRoleUpdatedEvent (
        User user,
        Role oldRole,
        Role newRole
){

}
