package com.sprint.mission.discodeit.service;



import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface UserService {
    User createUser(User user);  // 생성
    void readUser(UUID id);     // 단건 읽기
    List<User>readAllUsers();   // 모두 읽기
    void updateUser(User user);  // 수정
    void deleteUser(UUID Id);    // 삭제
}