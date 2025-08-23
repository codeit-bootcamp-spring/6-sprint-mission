package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessage implements MessageService {
    final List<Message> messageInfo = new ArrayList<>();


    @Override
    public void createMessage(Message message) {
        return;
    }

    @Override
    public Message readMessage(String Message) {
        return messageInfo.get(messageInfo.indexOf(Message));
    }


    @Override
    public List<Message> readAllMessages() {
        return List.of();
    }


    @Override
    public void updateMessage(Message message) {
        return;
    }

    @Override
    public void deleteMessage(String message) {
        return;
    }
}
