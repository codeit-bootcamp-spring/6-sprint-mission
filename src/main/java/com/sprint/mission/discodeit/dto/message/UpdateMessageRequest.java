package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotNull;

public record UpdateMessageRequest(
    @NotNull
    String newContent
) {

}
