package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
    @NotBlank
    String newUsername,
    @NotBlank
    String newEmail,
    String newPassword
) {

}
