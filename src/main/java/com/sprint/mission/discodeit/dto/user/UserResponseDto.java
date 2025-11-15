package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponseDto(
        UUID id,
        String email,
        String username,
        BinaryContentResponseDto profile,
        Boolean online
) {
}
