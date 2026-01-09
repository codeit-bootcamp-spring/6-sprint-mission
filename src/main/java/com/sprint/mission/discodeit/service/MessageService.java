package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final ApplicationEventPublisher eventPublisher;

    // 메시지 생성
    @Transactional
    public MessageResponseDto create(
            MessageCreateRequestDto request,
            List<BinaryContentCreateRequestDto> attachmentRequests
    ) {
        UUID userId = request.authorId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        Message message = Message.builder()
                .author(user)
                .channel(channel)
                .content(request.content())
                .build();
        messageRepository.save(message);
        saveAttachments(attachmentRequests, message);

        eventPublisher.publishEvent(new MessageCreatedEvent(user, channel, message.getContent()));
        log.info("메시지 생성이 완료되었습니다. id=" + message.getId());
        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    public MessageResponseDto findById(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toDto)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageResponseDto> findAllByChannelId(
            UUID channelId, Instant cursor, Pageable pageable
    ) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        Slice<MessageResponseDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                        Optional.ofNullable(cursor).orElse(Instant.now()),
                        pageable)
                .map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                    .createdAt();
        }

        return PageResponseMapper.fromSlice(slice, nextCursor);
    }

    // 내용 수정
    @Transactional
    @PreAuthorize("@messageService.isAuthor(#messageId, authentication.principal.id)")
    public MessageResponseDto update(UUID messageId, MessageUpdateRequestDto dto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        message.updateContent(dto.newContent());
        messageRepository.save(message);
        log.info("메시지 수정이 완료되었습니다. id=" + message.getId());

        return messageMapper.toDto(message);
    }

    // 삭제
    @Transactional
    @PreAuthorize("@messageService.isAuthor(#messageId, authentication.principal.id)")
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        messageRepository.delete(message);
        log.info("메시지 삭제가 완료되었습니다. id=" + messageId);
    }

    private void saveAttachments(List<BinaryContentCreateRequestDto> request, Message message) {
        List<BinaryContent> binaryContents = new ArrayList<>();
        for (BinaryContentCreateRequestDto image : request) {
            byte[] bytes = image.bytes();
            BinaryContent binaryContent = BinaryContent.createAttachmentImage(
                    image.fileName(),
                    image.contentType(),
                    (long) bytes.length,
                    message
            );
            binaryContentRepository.save(binaryContent);
            eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes));
            binaryContents.add(binaryContent);
        }
        message.setAttachments(binaryContents);
    }

    public boolean isAuthor(UUID messageId, UUID userId){
        return messageRepository.existsByIdAndAuthor_Id(messageId, userId);
    }
}
