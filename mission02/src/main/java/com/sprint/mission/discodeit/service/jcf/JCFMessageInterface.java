package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Exception.NotFoundException;
import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageInterface {
    private List<Message> messages;

    public JCFMessageInterface() {
        messages = new ArrayList<>();
    }

    public List<Message> getMessages(String userName) throws NotFoundException {
        if(messages.stream().noneMatch(n-> n.getSender().equals(userName))) {
            throw new NotFoundException("메세지가 없습니다.");
        }
        return messages.stream().filter(n->n.getSender().equals(userName)).toList();
    }
    public void addMessage(Message message) { messages.add(message); }

    public Message getMessage(UUID id) throws NotFoundException {
        return messages.stream().filter(n->n.getId().equals(id))
                .findFirst().orElseThrow(()->new NotFoundException("메세지가 없습니다."));
    }

    public Message getMessageByUserId(UUID id) throws NotFoundException {
        return messages.stream().filter(m -> m.getId().equals(id))
                .findFirst().orElseThrow(()-> new NotFoundException("메세지가 존재하지 않습니다."));
    }

    public List<Message> getMessagesByChannelId(UUID id) throws NotFoundException {
        return messages.stream().filter(m -> m.getChannelId().equals(id)).toList();
    }

    public void changeMessage(UUID id, String messageContext) {
        Message message = getMessageByUserId(id);
        message.setMessageContext(messageContext);
    }

    public void deleteMessage(UUID id) throws NotFoundException {
        if(messages.stream().noneMatch(m -> m.getId().equals(id))) {
            throw new NotFoundException("메세지가 없습니다.");
        }
        Message message = getMessage(id);
        messages.remove(message);

    }

}
