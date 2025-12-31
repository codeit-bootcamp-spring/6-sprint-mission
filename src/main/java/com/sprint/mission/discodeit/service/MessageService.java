package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDTO;
import com.sprint.mission.discodeit.dto.MessageDTO.Message;
import com.sprint.mission.discodeit.dto.PagingDTO;
import java.util.UUID;

public interface MessageService {

  MessageDTO.Message createMessage(MessageDTO.CreateMessageCommand request);

  boolean existMessageById(UUID id);

  MessageDTO.Message findMessageById(UUID id);

  PagingDTO.OffsetPage<MessageDTO.Message> findMessagesByAuthorId(UUID authorId,
      PagingDTO.OffsetRequest pageable);

  PagingDTO.OffsetPage<MessageDTO.Message> findMessagesByChannelId(UUID channelId,
      PagingDTO.OffsetRequest pageable);

  PagingDTO.CursorPage<MessageDTO.Message> findMessagesByChannelIdAndCreatedAt(UUID channelId,
      String createdAt, PagingDTO.CursorRequest pageable);

  PagingDTO.OffsetPage<Message> findAllMessages(PagingDTO.OffsetRequest pageable);

  MessageDTO.Message updateMessage(MessageDTO.UpdateMessageCommand request);

  void deleteMessageById(UUID id);

}
