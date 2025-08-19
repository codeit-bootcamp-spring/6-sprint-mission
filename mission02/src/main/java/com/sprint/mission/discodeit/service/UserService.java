package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelInterface;
import com.sprint.mission.discodeit.service.jcf.JCFMessageInterface;
import com.sprint.mission.discodeit.service.jcf.JCFUserInterface;

import java.util.List;
import java.util.UUID;

public class UserService {
    private final JCFUserInterface jcfUserInterface = new JCFUserInterface();
    private final JCFChannelInterface jcfChannelInterface = new JCFChannelInterface();
    private final JCFMessageInterface jcfMessageInterface = new JCFMessageInterface();

    public void changeUserName(UUID id, String userName){
        jcfUserInterface.changeUserName(id, userName);
    }

    public void addUser(User user) {
        jcfUserInterface.addUser(user);
    }

    public User getUserInformation(UUID id){
        return jcfUserInterface.getUserById(id);
    }

    public void addChannel(UUID id, Channel channel){
        jcfChannelInterface.addChannel(channel);
        jcfUserInterface.acceptChannel(id, channel);
    }

    public List<Message> getMessages(String userName){
        return  jcfMessageInterface.getMessages(userName);
    }
}
