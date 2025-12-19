package com.sprint.mission.discodeit.dto.request;

import lombok.Builder;

@Builder
public record UpdateUserRequest(
    String newUsername,
    String newEmail,
    String newPassword
) {

}
