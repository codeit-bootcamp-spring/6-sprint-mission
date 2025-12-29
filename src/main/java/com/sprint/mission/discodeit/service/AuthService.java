package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.TokenDTO;

public interface AuthService {

  //UserDTO.User login(UserDTO.LoginCommand loginCommand);
  TokenDTO renewAccessToken(String token);

}
