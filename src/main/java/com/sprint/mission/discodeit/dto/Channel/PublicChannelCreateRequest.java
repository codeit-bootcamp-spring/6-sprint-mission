package com.sprint.mission.discodeit.dto.Channel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PublicChannelCreateRequest(
        @NotBlank(message = "채널 이름은 있어야합니다")
        String name,

        String description
) {
}
