package com.sprint.mission.discodeit.dto.Channel;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PrivateChannelCreateRequest (
        @NotEmpty(message = "유저는 입력은 필수 입니다")
        List<UUID> participantIds
) {
}
