package com.sprint.mission.discodeit.dto.Message;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MessageUpdateRequest(
        @NotBlank
        String newContent
) {}
