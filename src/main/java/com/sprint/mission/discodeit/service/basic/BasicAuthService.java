package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.mapper.UserEntityMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserEntityMapper userEntityMapper;
  private final PasswordEncoder passwordEncoder;

  /*@Override
  public UserDTO.User login(UserDTO.LoginCommand loginCommand) {

    UserEntity userEntity = userRepository.findByUsername(loginCommand.username())
        .orElseThrow(NoSuchUserException::new);

    if (userEntity.getPassword().equals(passwordEncoder.encode(loginCommand.password()))) {
      return userEntityMapper.toUser(userEntity);
    } else {
      throw new PasswordMismatchException();
    }

  }*/
}
