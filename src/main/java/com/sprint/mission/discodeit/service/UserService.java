package com.sprint.mission.discodeit.service;



import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    void createUser(User user);  // 생성
    User readUser(UUID Id);     // 단건 읽기
    List<User>readAllUsers();   // 모두 읽기
    void updateUser(User user);  // 수정
    void deleteUser(UUID Id);    // 삭제
}