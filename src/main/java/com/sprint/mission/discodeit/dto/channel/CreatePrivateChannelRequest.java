package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreatePrivateChannelRequest(
    @NotEmpty
    List<@NotNull UUID> participantIds
) {

}
