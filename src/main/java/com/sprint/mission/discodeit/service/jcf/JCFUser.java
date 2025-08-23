package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUser implements UserService {
    List<User> userInfo = new ArrayList<>();


    @Override
    public User createUser(User user) {
        return user;
    }

    @Override
    public Optional<User> readUser(UUID Id) {
        return userInfo.get(userInfo.indexOf(Id));
    }


    @Override
    public List<User> readAllUsers() {
        return List.of();
    }


    @Override
    public Optional<User> updateUser(User user) {
        return;
    }

    @Override
    public boolean deleteUser(UUID Id) {
        return false;
    }
}
