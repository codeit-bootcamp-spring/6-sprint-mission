package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    // 생성자를 통해 Repository 의존성 주입
    public BasicUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User read(String name){
        return userRepository.findall().stream().filter(u -> u.getName().equals(name)).findAny().orElse(null);
    }
    public User create(String name){
        User user = new User(name);
        userRepository.save(user);
        return user;
    }
    public List<User> allRead(){
        return userRepository.findall();
    }
    public User modify(UUID id, String name){
        User user = userRepository.findall().stream().filter(u->u.getCommon().getId().equals(id)).findAny().orElse(null);
        if (user != null) {
            user.setName(name);
            return user;
        } else {
            System.out.println("해당 유저 없음");
            return null;
        }
    }
    public void delete(UUID id){
        userRepository.delete(id);
    }

}
