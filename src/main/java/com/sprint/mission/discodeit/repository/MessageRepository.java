package com.sprint.mission.discodeit.repository;

<<<<<<< HEAD
<<<<<<< HEAD

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
=======
=======
>>>>>>> 박지훈
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    void createMessage(Message message);
    Message findMessageById(UUID id);
    List<Message> findAllMessages();
    void updateMessage(Message message);
    void deleteMessage(UUID id);
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈

}
