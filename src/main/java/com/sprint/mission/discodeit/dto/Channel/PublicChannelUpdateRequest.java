package com.sprint.mission.discodeit.dto.Channel;

import lombok.Builder;

@Builder
public record PublicChannelUpdateRequest(
        String newName,
        String newDescription
) {
}
