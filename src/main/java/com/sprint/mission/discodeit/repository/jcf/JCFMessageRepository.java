package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
<<<<<<< HEAD
<<<<<<< HEAD

import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
@Repository
=======
=======
>>>>>>> 박지훈
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======

import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
@Repository
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
public class JCFMessageRepository implements MessageRepository {
    public final List<Message> MessageDate;

    public JCFMessageRepository() {
        MessageDate = new ArrayList<Message>();
    }

<<<<<<< HEAD
<<<<<<< HEAD
    @Override
    public Message save(Message message) {
        int idx = MessageDate.indexOf(message);
        if (idx >=0) {
            MessageDate.set(idx, message);
        }else {
            MessageDate.add(message);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {

        return MessageDate.stream()
                .filter(message -> message.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Message> findAll() {

        return List.copyOf(MessageDate);
    }

    @Override
    public boolean existsById(UUID id) {

        return MessageDate.stream()
                .anyMatch(message -> message.getId().equals(id));
    }

    @Override
    public void deleteById(UUID id) {

        MessageDate.removeIf(message -> message.getId().equals(id));
=======
=======
>>>>>>> 박지훈
    public void createMessage(Message message) {
        MessageDate.add(message);
    }


    public Message findMessageById(UUID id) {
        return MessageDate.stream()
                .filter(message -> message.getMessageId().equals(id))
                .findAny()
                .orElse(null);
    }

    public List<Message> findAllMessages() {
        return MessageDate;
    }


    public void updateMessage(Message message) {

        int idx = MessageDate.indexOf(message);
        if (idx == -1) {
            throw new NullPointerException("해당 메시지 없습니다.");
        }
        MessageDate.set(idx, message);

    }

    public void deleteMessage(UUID id) {
        MessageDate.remove(findMessageById(id));
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
    @Override
    public Message save(Message message) {
        int idx = MessageDate.indexOf(message);
        if (idx >=0) {
            MessageDate.set(idx, message);
        }else {
            MessageDate.add(message);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {

        return MessageDate.stream()
                .filter(message -> message.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Message> findAll() {

        return List.copyOf(MessageDate);
    }

    @Override
    public boolean existsById(UUID id) {

        return MessageDate.stream()
                .anyMatch(message -> message.getId().equals(id));
    }

    @Override
    public void deleteById(UUID id) {

        MessageDate.removeIf(message -> message.getId().equals(id));
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
    }
}
