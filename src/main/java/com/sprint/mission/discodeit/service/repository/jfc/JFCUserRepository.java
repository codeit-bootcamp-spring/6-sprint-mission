package com.sprint.mission.discodeit.service.repository.jfc;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.repository.UserRepositoryInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JFCUserRepository implements UserRepositoryInterface {
    private Map<UUID, User> data;

    // 생성자
    public JFCUserRepository(){ data = new HashMap<>();}

    // 유저 생성
    @Override
    public User createUser(String name, String status, String email){
        User newUser = new User(name, status, email);
        data.put(newUser.getId(), newUser);
        return newUser;
    }

    // 유저 아이디로 특정 유저 조회
    @Override
    public User findById(UUID userId){
        if(!data.containsKey(userId)){ // 존재하지 않을 시 null 반환
            return null;
        }
        return data.get(userId); // 존재할 시 user 객체 참조값 반환
    }
    // 전체 유저 조회
    @Override
    public List<User> findAll(){
        return data.values().stream().toList(); // 아무 유저도 존재하지 않을 시 빈 List 반환
    }

    public void updateName(User user, String updatedName){
        user.setName(updatedName);
    }

    // 유저 상태 수정
    @Override
    public void updateStatus(User user, String updatedStatus){
        user.setStatus(updatedStatus);
    }
    // 유저 이메일 수정
    @Override
    public void updateEmail(User user, String updatedEmail){
        user.setEmail(updatedEmail);
    }

    @Override
    public void deleteUser(User user) {
        data.remove(user.getId());
    }

    // 인터페이스에 의해 구현한 깡통 메서드 (file~repository와 호환되기 위해...)
    @Override
    public void saveData() { data = data; }

    // 유저 존재 여부 확인
    @Override
    public boolean notExist(User user) {
        return !data.containsKey(user.getId());
    }

}
