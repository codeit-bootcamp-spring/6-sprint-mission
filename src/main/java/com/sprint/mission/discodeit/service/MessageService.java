package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, UUID channelId, UUID authorId);

    Message create(CreateMessageRequest request);  //생성메세지요청

    Message find(UUID authorId);

    List<Message> findAllByChannelId(UUID channelId);

    List<Message> findAll();

    Message update(UUID messageId, String newContent);

    Message update(UpdateMessageRequest request);

    void delete(UUID messageId);
}