package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
    @NotBlank
    String newUsername,
    @NotBlank
    String newEmail,
    String newPassword
) {

}
