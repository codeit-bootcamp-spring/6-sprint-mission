package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.LoginDto;

public interface AuthService {
    void userMatch(LoginDto loginDto);

}
