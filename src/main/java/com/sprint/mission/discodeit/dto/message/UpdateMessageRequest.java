package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateMessageRequest(
    @NotNull
    String newContent
) {

}
