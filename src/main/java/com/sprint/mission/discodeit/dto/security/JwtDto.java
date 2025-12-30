package com.sprint.mission.discodeit.dto.security;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import lombok.Builder;

@Builder
public record JwtDto(
        UserResponseDto dto,
        String accessToken
) {

}
