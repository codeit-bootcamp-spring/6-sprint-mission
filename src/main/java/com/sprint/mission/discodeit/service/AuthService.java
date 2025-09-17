package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto.LoginDto;

public interface AuthService {
    void userMatch(LoginDto loginDto);

}
