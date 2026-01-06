package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.User.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtInformation {
    private UserDto userDto;
    private String accessToken;
    private String refreshToken;
}
