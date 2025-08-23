package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository {
    User createUser(User user);  // 생성
        // 단건 읽기
    Optional<User> readUser(UUID Id);
    List<User> readAllUsers();   // 모두 읽기
    Optional<User> updateUser(User user);  // 수정
    boolean deleteUser(UUID Id);  // 삭제
}