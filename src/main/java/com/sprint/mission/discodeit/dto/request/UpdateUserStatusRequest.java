package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UpdateUserStatusRequest(
    @NotNull
    Instant newLastActiveAt
) {

}
