package com.sprint.mission.discodeit.service;

<<<<<<< HEAD
<<<<<<< HEAD
import com.sprint.mission.discodeit.DTO.User.CreateUserDTO;
import com.sprint.mission.discodeit.DTO.User.FindUserDTO;
import com.sprint.mission.discodeit.DTO.User.UpdateUserDTO;
=======
>>>>>>> 박지훈
=======
=======
import com.sprint.mission.discodeit.DTO.User.CreateUserDTO;
import com.sprint.mission.discodeit.DTO.User.FindUserDTO;
import com.sprint.mission.discodeit.DTO.User.UpdateUserDTO;
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
<<<<<<< HEAD
<<<<<<< HEAD
      User create(CreateUserDTO createUserDTO);
      FindUserDTO find(UUID id);
      FindUserDTO findEmail(String email);
      List<FindUserDTO> findAll();
      void update(UpdateUserDTO updateUserDTO);
      void delete(UUID id);
=======
=======
>>>>>>> 박지훈
      void createUser(String username, int age , String email);
      User findUserById(UUID id);
      User findUserByEmail(String userEmail);
      List<User> findAllUsers();
      void updateUser(User user);
      void deleteUser(UUID id);
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
      User create(CreateUserDTO createUserDTO);
      FindUserDTO find(UUID id);
      FindUserDTO findEmail(String email);
      List<FindUserDTO> findAll();
      void update(UpdateUserDTO updateUserDTO);
      void delete(UUID id);
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
}
