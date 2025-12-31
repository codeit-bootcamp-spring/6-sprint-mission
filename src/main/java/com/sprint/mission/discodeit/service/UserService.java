package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDTO;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserDTO.User createUser(UserDTO.CreateUserCommand request);

  boolean existUserById(UUID id);

  boolean existUserByEmail(String email);

  boolean existUserByUsername(String nickname);

  UserDTO.User findUserById(UUID id);

  UserDTO.User findUserByEmail(String email);

  UserDTO.User findUserByUsername(String username);

  List<UserDTO.User> findAllUsers();

  UserDTO.User updateUser(UserDTO.UpdateUserCommand request);

  UserDTO.User updateUserRole(UserDTO.UpdateUserRoleCommand request);

  void deleteUserById(UUID id);

}
