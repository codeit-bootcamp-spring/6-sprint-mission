package com.sprint.mission.discodeit.dto.Auth;

import com.sprint.mission.discodeit.dto.User.UserDto;
import lombok.Builder;

@Builder
public record JwtDto(
        UserDto userDto,
        String accessToken
) {
}
