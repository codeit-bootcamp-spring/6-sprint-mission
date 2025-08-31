package com.sprint.mission.discodeit.service.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryInterface {
    // 유저 생성
    User createUser(String name, String status, String email);

    // 유저 아이디로 특정 유저 조회
    User findById(UUID userId);

    // 전체 유저 조회
    List<User> findAll();

    void updateName(User user, String updatedName);

    // 유저 상태 수정
    void updateStatus(User user, String updatedStatus);

    // 유저 이메일 수정
    void updateEmail(User user, String updatedEmail);

    void deleteUser(User user);

    // 인터페이스에 의해 구현한 깡통 메서드 (file~repository와 호환되기 위해...)
    void saveData();

    // 유저 존재 여부 확인
    boolean notExist(User user);
}
