package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Exception.NotFoundException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserInterface {
    private final List<User> userList;

    public JCFUserInterface() { userList = new ArrayList<User>(); }

    public List<User> getAllUsers() { return userList; }

    public void addUser(User user) { userList.add(user); }

    public User getUserById(UUID id) throws NotFoundException {
        User user = userList.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (user == null) {
            throw new NotFoundException("사용자가 존재하지 않습니다.");
        }
        return user;
    }
    public User getUserByName(String name) throws NotFoundException {
        User user = userList.stream().filter(n->n.getUserName().equals(name)).findFirst().orElse(null);
        if(user == null){
            throw new NotFoundException("사용자가 존재하지 않습니다.");
        }
        return user;
    }

    public void changeUserName(UUID id, String name) throws NotFoundException {
        try{
            getUserById(id).setUserName(name);
            getUserById(id).setUpdatedAt(System.currentTimeMillis());
        } catch (NotFoundException e) {
            throw new NotFoundException("사용자가 존재하지 않습니다.");
        }
    }

    public void deleteUserById(UUID id) throws NotFoundException {
        try{
            User user = getUserById(id);
            userList.remove(user);
        } catch (NotFoundException e) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    public void acceptChannel(UUID id, Channel channel) throws NotFoundException {
        try{
            User user = getUserById(id);
            user.setChannels(channel);
        } catch (NotFoundException e) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

}
