package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // *저장로직* 유저 생성 (+전체 유저 관리 Map에 추가 + saveData)
    User createUser(String name, String status, String email);

    // *저장로직* 유저 아이디로 특정 유저 조회
    User findById(UUID userId);

    // *저장로직* 전체 유저 조회
    List<User> findAll();

    // *비즈니스로직* 특정 채널의 모든 유저 조회
    List<User> findAllUsersByChannelId(UUID channelId);

    // *비즈니스로직* 유저 이름 수정
    boolean updateName(User user, String updatedName);

    // *저장로직* 유저 상태 수정
    boolean updateStatus(User user, String updatedStatus);

    // *저장로직* 유저 이메일 수정
    boolean updateEmail(User user, String updatedEmail);

    // *비즈니스로직* 유저 삭제
    boolean deleteUser(User user);

    boolean notExist(User user);
}
