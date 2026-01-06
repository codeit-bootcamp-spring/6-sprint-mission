package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDTO;
import com.sprint.mission.discodeit.dto.MessageDTO.Message;
import com.sprint.mission.discodeit.dto.PagingDTO;
import com.sprint.mission.discodeit.dto.PagingDTO.OffsetPage;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.event.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.channel.NoSuchChannelException;
import com.sprint.mission.discodeit.exception.message.NoSuchMessageException;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.mapper.MessageEntityMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final MessageEntityMapper messageEntityMapper;

  @PreAuthorize("hasRole('USER')")
  @Transactional
  @Override
  public MessageDTO.Message createMessage(MessageDTO.CreateMessageCommand request) {

    if (!userRepository.existsById(request.userId())) {
      log.warn("User with id {} does not exist", request.userId());
      throw new NoSuchUserException();
    }

    if (!channelRepository.existsById(request.channelId())) {
      log.warn("Channel with id {} does not exist", request.channelId());
      throw new NoSuchChannelException();
    }

    MessageEntity messageEntity = MessageEntity.builder()
        .author(userRepository.findById(request.userId())
            .orElseThrow(NoSuchUserException::new))
        .channel(channelRepository.findById(request.channelId())
            .orElseThrow(NoSuchChannelException::new))
        .content(request.content())
        .build();

    if (!request.binaryContentList().isEmpty()) {

      List<BinaryContentEntity> binaryContentList = binaryContentRepository.saveAll(
          request.binaryContentList().stream()
              .map(binaryContent -> BinaryContentEntity.builder()
                  .fileName(binaryContent.fileName())
                  .size((long) binaryContent.data().length)
                  .contentType(binaryContent.contentType())
                  .build())
              .toList());

      for (int i = 0; i < binaryContentList.size(); i++) {
        eventPublisher.publishEvent(
            BinaryContentCreatedEvent.of(
                binaryContentList.get(i).getId(),
                request.binaryContentList().get(i).data()
            )
        );
      }

    }

    log.debug("Creating message with id {}", messageEntity.getId());

    return messageEntityMapper.toMessage(messageRepository.save(messageEntity));

  }

  @Override
  public boolean existMessageById(UUID id) {
    return messageRepository.existsById(id);
  }

  @Override
  public MessageDTO.Message findMessageById(UUID id) {

    MessageEntity messageEntity = messageRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Message with id {} does not exist", id);
          throw new NoSuchMessageException();
        });

    return messageEntityMapper.toMessage(messageEntity);
  }

  @Transactional(readOnly = true)
  @Override
  public OffsetPage<MessageDTO.Message> findMessagesByAuthorId(UUID authorId,
      PagingDTO.OffsetRequest pageable) {

    if (!userRepository.existsById(authorId)) {
      log.warn("User with id {} does not exist", authorId);
      throw new NoSuchUserException();
    }

    Page<MessageEntity> paging = messageRepository.findByAuthorId(authorId,
        PageRequest.of(pageable.getPage(), pageable.getSize()));

    return OffsetPage.<MessageDTO.Message>builder()
        .content(paging.getContent().stream()
            .map(messageEntityMapper::toMessage)
            .toList())
        .number(paging.getNumber())
        .size(paging.getSize())
        .hasNext(paging.hasNext())
        .totalElement(paging.getTotalElements())
        .build();

  }

  @Transactional(readOnly = true)
  @Override
  public PagingDTO.OffsetPage<MessageDTO.Message> findMessagesByChannelId(UUID channelId,
      PagingDTO.OffsetRequest pageable) {

    if (!channelRepository.existsById(channelId)) {
      log.warn("Channel with id {} does not exist", channelId);
      throw new NoSuchChannelException();
    }

    Sort.Direction direction =
        pageable.getSort().split(",")[1].equalsIgnoreCase("DESC") ? Direction.DESC : Direction.ASC;

    Page<MessageEntity> paging = messageRepository.findByChannelId(channelId,
        PageRequest.of(pageable.getPage(), pageable.getSize(), direction,
            pageable.getSort().split(",")[0]));

    return PagingDTO.OffsetPage.<MessageDTO.Message>builder()
        .content(paging.getContent().stream()
            .map(messageEntityMapper::toMessage)
            .toList())
        .number(paging.getNumber())
        .size(paging.getSize())
        .hasNext(paging.hasNext())
        .totalElement(paging.getTotalElements())
        .build();

  }

  @Transactional(readOnly = true)
  @Override
  public PagingDTO.CursorPage<Message> findMessagesByChannelIdAndCreatedAt(UUID channelId,
      String createdAt, PagingDTO.CursorRequest pageable) {

    if (!channelRepository.existsById(channelId)) {
      log.warn("Channel with id {} does not exist", channelId);
      throw new NoSuchChannelException();
    }

    Slice<MessageEntity> slice = messageRepository.findByChannelIdAndCreatedAt(channelId,
        Instant.parse(createdAt), pageable.getSize());

    return PagingDTO.CursorPage.<Message>builder()
        .content(slice.getContent().stream()
            .map(messageEntityMapper::toMessage)
            .toList())
        .nextCursor(slice.hasNext() ? messageEntityMapper.toMessage(
            slice.getContent().get(slice.getContent().size() - 1)) : null)
        .size(slice.getSize())
        .hasNext(slice.hasNext())
        .build();

  }

  @Override
  public OffsetPage<Message> findAllMessages(PagingDTO.OffsetRequest pageable) {

    Page<MessageEntity> paging = messageRepository.findAll(
        PageRequest.of(pageable.getPage(), pageable.getSize()));

    return OffsetPage.<MessageDTO.Message>builder()
        .content(paging.getContent().stream()
            .map(messageEntityMapper::toMessage)
            .toList())
        .number(paging.getNumber())
        .size(paging.getSize())
        .hasNext(paging.hasNext())
        .totalElement(paging.getTotalElements())
        .build();

  }

  @PreAuthorize("hasPermission(#request.id(), 'MESSAGE', 'UPDATE')")
  @Transactional
  @Override
  public MessageDTO.Message updateMessage(MessageDTO.UpdateMessageCommand request) {

    if (!messageRepository.existsById(request.id())) {
      log.warn("Message with id {} does not exist", request.id());
      throw new NoSuchMessageException();
    }

    MessageEntity updatedMessageEntity = messageRepository.findById(request.id())
        .orElseThrow(NoSuchMessageException::new);
    updatedMessageEntity.updateMessage(request.content());

    log.debug("Updating message with id {}", updatedMessageEntity.getId());

    return messageEntityMapper.toMessage(messageRepository.save(updatedMessageEntity));

  }

  @PreAuthorize("hasPermission(#request.id(), 'MESSAGE', 'DELETE')")
  @Transactional
  @Override
  public void deleteMessageById(UUID id) {

    binaryContentRepository.deleteAllByIdIn(messageRepository.findById(id)
        .orElseThrow(NoSuchMessageException::new).getAttachments()
        .stream()
        .map(BinaryContentEntity::getId)
        .toList());

    messageRepository.deleteById(id);

    log.debug("Deleted message with id {}", id);

  }
}
