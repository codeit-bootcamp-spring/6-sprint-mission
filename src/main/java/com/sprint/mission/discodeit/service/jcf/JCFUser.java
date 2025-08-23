package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUser implements UserService {
    List<User> users = new ArrayList<>();


    @Override
    public User createUser(User user) {
        return user;
    }

    @Override
    public void readUser(UUID Id) {
        return;
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

