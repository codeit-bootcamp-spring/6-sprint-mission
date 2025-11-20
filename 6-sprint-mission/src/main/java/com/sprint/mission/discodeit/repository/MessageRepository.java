package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MessageRepository {
    Message find(UUID id);

    Message save(Message message);

    List<Message> findall();

    void delete(UUID id);
}
