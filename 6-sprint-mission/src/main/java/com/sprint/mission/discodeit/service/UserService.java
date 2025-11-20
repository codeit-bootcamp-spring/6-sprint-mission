package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User read(String name);
    User create(String name);
    List<User> allRead();
    User modify(UUID id, String name);
    void delete(UUID id);
}
