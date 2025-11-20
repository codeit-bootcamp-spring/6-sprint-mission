package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap;

    public JCFMessageRepository() {
        this.messageMap = new HashMap<>();
    }

    @Override
    public Message find(UUID id) {
        return messageMap.get(id);
    }

    @Override
    public Message save(Message message) {
        UUID id = message.getCommon().getId();
        Message existMessage = messageMap.get(id);
        if(existMessage!=null){
            return existMessage;
        }
        messageMap.put(id,message);
        return message;
    }

    @Override
    public List<Message> findall() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public void delete(UUID id) {
        messageMap.remove(id);
    }

}
