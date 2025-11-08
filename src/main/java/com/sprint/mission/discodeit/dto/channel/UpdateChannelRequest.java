package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateChannelRequest(
    @NotBlank
    String newName,
    @NotBlank
    String newDescription
) {

}
