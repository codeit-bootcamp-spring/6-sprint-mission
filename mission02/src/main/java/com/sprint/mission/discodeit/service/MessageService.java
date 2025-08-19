package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFChannelInterface;
import com.sprint.mission.discodeit.service.jcf.JCFMessageInterface;

import java.util.List;
import java.util.UUID;

public class MessageService {
    private final JCFMessageInterface jcfMessageInterface = new JCFMessageInterface();

    public void addMessage(Message message){
        jcfMessageInterface.addMessage(message);
    }

    public void changeMessage(UUID id, String messageContext){
        jcfMessageInterface.changeMessage(id, messageContext);
    }

    public void deleteMessage(UUID id){
        jcfMessageInterface.deleteMessage(id);
    }

    public List<Message> getAllMessages(String userName){
        return jcfMessageInterface.getMessages(userName);
    }

    public List<Message> getMessagesByChannelId(UUID id){
        return jcfMessageInterface.getMessagesByChannelId(id);
    }
}
