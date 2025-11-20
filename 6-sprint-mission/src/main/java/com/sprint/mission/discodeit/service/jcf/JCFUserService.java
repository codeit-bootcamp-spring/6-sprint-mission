package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final List<User> userData;

    public JCFUserService() {
        this.userData = new ArrayList<>();
    }

    @Override
    public User read(String name) {
        return userData.stream().filter(user->user.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        userData.add(user);
        return user;
    }

    @Override
    public List<User> allRead() {
        return userData;
    }

    @Override
    public User modify(UUID id, String name) {
        User user = userData.stream().filter(u->u.getCommon().getId().equals(id)).findAny().orElse(null);
        if(user!=null){
            user.setName(name);
            return user;
        } else{
            System.out.println("해당 유저 없음");
            return null;
        }
    }

    @Override
    public void delete(UUID id) {
        User user = userData.stream().filter(u->u.getCommon().getId().equals(id)).findAny().orElse(null);
        userData.remove(user);
    }
}
