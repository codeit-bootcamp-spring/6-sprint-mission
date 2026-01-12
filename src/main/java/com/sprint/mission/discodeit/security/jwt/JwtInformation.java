package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;

import java.util.UUID;

public record JwtInformation (
        UserResponseDto dto,
        String accessToken,
        String refreshToken
    ){
    public UUID getUserId() {
        return dto.getId();
    }
}
