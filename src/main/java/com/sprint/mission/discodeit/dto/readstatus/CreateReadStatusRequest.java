package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusRequest(
    @NotNull
    Instant lastReadAt,
    @NotNull
    UUID userId,
    @NotNull
    UUID channelId
) {

}
