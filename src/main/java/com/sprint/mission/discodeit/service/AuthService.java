package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.TokenDTO;

public interface AuthService {

  TokenDTO renewAccessToken(String token);

}
