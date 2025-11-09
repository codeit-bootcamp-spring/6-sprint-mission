package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage storage;

  @Override
  public Message create(CreateMessageRequest createMessageRequest,
      List<MultipartFile> attachments) {
    Channel channel = channelRepository.findById(createMessageRequest.channelId())
        .orElseThrow(() -> {
          log.warn("Channel Not Found. channelId: {}", createMessageRequest.channelId());
          return new ChannelNotFoundException();
        });
    User author = userRepository.findById(createMessageRequest.authorId())
        .orElseThrow(() -> {
          log.warn("User not found. userId: {}", createMessageRequest.authorId());
          return new UserNotFoundException(Map.of("유저 고유 아이디", createMessageRequest.authorId()));
        });

    List<MultipartFile> attachmentsNotNull =
        attachments != null ? attachments : Collections.emptyList();

    List<BinaryContent> binaryContents = attachmentsNotNull.stream().map(
        file -> {
          try {
            BinaryContent binaryContent = new BinaryContent(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
            );
            BinaryContent saved = binaryContentRepository.save(binaryContent);
            storage.put(saved.getId(), file.getBytes());
            return saved;
          } catch (IOException e) {
            log.error("첨부파일 처리 실패", e);
            throw new RuntimeException("첨부파일 처리 실패");
          }
        }
    ).toList();

    Message message = new Message(createMessageRequest.content(), channel, author, binaryContents);
    Message saved = messageRepository.save(message);

    log.info("메시지 생성: {}", message.getId());
    if (!binaryContents.isEmpty()) {
      log.info("첨부파일 업로드: {}", binaryContents.stream().map(BaseEntity::getId).toList());
    }
    log.debug("작성자: {}, 내용: {}", author.getUsername(), message.getContent());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("Message not found. messageId: {}", messageId);
          return new MessageNotFoundException();
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
    return messageRepository.findAllByChannel_IdAndCreatedAtLessThanEqual(
        channelId,
        cursor != null ? cursor : Instant.now(),
        pageable
    );
  }

  @Override
  public Message update(UUID messageId, UpdateMessageRequest updateMessageRequest) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("Message not found. messageId: {}", messageId);
          return new MessageNotFoundException();
        });
    message.update(updateMessageRequest.newContent());
    Message updated = messageRepository.save(message);

    log.info("메시지 업데이트: {}", updated.getId());
    log.debug("내용: {}", updated.getContent());
    return updated;
  }

  @Override
  public void delete(UUID messageId) {
    if (!messageRepository.existsById(messageId)) {
      log.warn("Message not found. messageId: {}", messageId);
      throw new MessageNotFoundException();
    }
    // 메시지의 첨부파일들 객체 삭제
    Message message = messageRepository.findById(messageId).orElse(null);
    List<UUID> binaryContentIds = message.getAttachments()
        .stream()
        .map(BaseEntity::getId)
        .toList();
    binaryContentRepository.deleteAllById(binaryContentIds);
    // 메시지 id로 삭제
    messageRepository.deleteById(messageId);

    log.info("메시지 삭제: {}", messageId);
  }
}
