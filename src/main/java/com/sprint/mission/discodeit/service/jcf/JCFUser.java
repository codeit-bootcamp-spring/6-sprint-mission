package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUser implements UserService {
    List<User> userInfo = new ArrayList<>();


    @Override
    public void createUser(User user) {
        return;
    }

    @Override
    public User readUser(UUID Id) {
        return userInfo.get(userInfo.indexOf(Id));
    }


    @Override
    public List<User> readAllUsers() {
        return List.of();
    }


    @Override
    public void updateUser(User user) {
        return;
    }

    @Override
    public void deleteUser(UUID Id) {
        return;
    }
}