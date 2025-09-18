package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Optional;
=======
>>>>>>> 박지훈
=======
=======
import java.util.Optional;
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
import java.util.UUID;

public interface UserRepository {

    //기존 CRUD
<<<<<<< HEAD
<<<<<<< HEAD
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);

=======
=======
>>>>>>> 박지훈
    void createUser(User user);
    User findUserById(UUID id);
    User findUserByEmail(String userEmail);
    List<User> findAllUsers();
    void updateUser(User user);
    void deleteUser(UUID id);

    /*
    //구현 순위 낮음
    //유저에 서버 추가
    void addUserToChannel(Channel channel, User user);
    void removeUserFromChannel(Channel channel, User user);

    //친구 추가
    void addUserToFriend(User user, UUID id);
    void removeUserFromFriend(User user, UUID id);
     */
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);

>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
}
