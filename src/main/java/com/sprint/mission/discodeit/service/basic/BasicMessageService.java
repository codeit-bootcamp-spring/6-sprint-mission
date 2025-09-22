package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException(channelId+"not found");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException(authorId+"not found");
        }

        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }

    @Override
    public Message create(CreateMessageRequest request) {
        return create(request.content(), request.channelId(), request.authorId());
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException(messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }


    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException(messageId + " not found"));
        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    public Message update(UpdateMessageRequest request) {
        return update(request.messageId(), request.newContent());
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException(messageId + " not found");
        }
        messageRepository.deleteById(messageId);
    }
}
