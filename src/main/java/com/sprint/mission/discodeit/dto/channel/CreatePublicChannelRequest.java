package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreatePublicChannelRequest(
    @NotBlank(message = "channel.name.required")
    @Size(max = 100)
    String name,
    @NotBlank(message = "channel.description.required")
    @Size(max = 500)
    String description
) {

}
