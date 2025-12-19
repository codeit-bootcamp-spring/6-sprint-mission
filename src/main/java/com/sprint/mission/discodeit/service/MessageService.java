package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  Message create(CreateMessageRequest createMessageRequest, List<MultipartFile> attachments);

  Message find(UUID messageId);

  Slice<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

  Message update(UUID messageId, UpdateMessageRequest updateMessageRequest);

  void delete(UUID messageId);
}
