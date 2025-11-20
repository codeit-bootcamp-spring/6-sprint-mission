package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> userMap;

    public JCFUserRepository() {
        this.userMap = new HashMap<>();
    }

    @Override
    public User find(UUID id) {
        return userMap.get(id);
    }

    @Override
    public User save(User user) {
        UUID id = user.getCommon().getId();
        User existUser = userMap.get(id);
        if(existUser!=null){
            return existUser;
        }
        userMap.put(id,user);
        return user;
    }

    @Override
    public List<User> findall() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void delete(UUID id) {
        userMap.remove(id);
    }

}
