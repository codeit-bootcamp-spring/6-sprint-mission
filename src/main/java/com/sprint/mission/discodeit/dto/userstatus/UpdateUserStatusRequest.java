package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UpdateUserStatusRequest(
    @NotNull
    Instant newLastActiveAt
) {

}
