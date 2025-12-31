package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class JwtDto {

    private UserDto userDto;
    private String accessToken;

    public static JwtDto of(UserDto userDto, String accessToken) {
        return JwtDto.builder()
            .userDto(userDto)
            .accessToken(accessToken)
            .build();
    }
}
