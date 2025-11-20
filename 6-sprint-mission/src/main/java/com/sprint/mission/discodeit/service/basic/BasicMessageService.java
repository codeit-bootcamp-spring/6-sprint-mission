package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    // 생성자를 통해 Repository 의존성 주입
    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message read(String sender) {
        return messageRepository.findall().stream().filter(ch -> ch.getSender().equals(sender)).findAny().orElse(null);
    }

    public Message create(User sender, User reciever, String content, Channel channel) {
        Message message = new Message(sender, reciever, content, channel);
        messageRepository.save(message);
        return message;
    }

    public List<Message> allRead() {
        return messageRepository.findall();
    }

    public Message modify(UUID id, String content) {
        Message message = messageRepository.findall().stream().filter(msg -> msg.getCommon().getId().equals(id)).findAny().orElse(null);
        if (message != null) {
            message.setContent(content);
            return message;
        } else {
            System.out.println("해당 유저 없음");
            return null;
        }
    }

    public void delete(UUID id) {
        messageRepository.delete(id);
    }
}
