package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message read(String sender);
    Message create(User sender, User reciever, String content, Channel channel);
    List<Message> allRead();
    Message modify(UUID id, String content);
    void delete(UUID id);
}
