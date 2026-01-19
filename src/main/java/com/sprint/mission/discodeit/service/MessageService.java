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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final String TEMP_FILE_PREFIX = "binary_";
    private static final String TEMP_FILE_EXTENSION = ".tmp";

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
        if (attachmentRequests != null) {
            saveAttachments(attachmentRequests, message);
        }
        eventPublisher.publishEvent(new MessageCreatedEvent(user, channel, message));
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

        List<BinaryContent> binaryContents = request.stream()
                .map(dto ->
                        BinaryContent.createAttachmentImage(dto.fileName(), dto.contentType(), (long) dto.bytes().length, message)
                ).toList();
        binaryContentRepository.saveAll(binaryContents);

        for (int i = 0; i < binaryContents.size(); i++) {
            BinaryContent entity = binaryContents.get(i);
            BinaryContentCreateRequestDto dto = request.get(i);
            try {
                // OS가 지정한 기본 임시 디렉토리에 임시 파일 생성
                // 저장 예시) binary_123456789_id~~.tmp
                Path tempFile = Files.createTempFile(TEMP_FILE_PREFIX, "_" + entity.getId() + TEMP_FILE_EXTENSION);

                Files.write(tempFile, dto.bytes());

                eventPublisher.publishEvent(new BinaryContentCreatedEvent(entity.getId(), tempFile));
            } catch (IOException e) {
                log.error("임시 파일 생성 실패", e);
                throw new RuntimeException("임시 파일 저장 중 오류가 발생했습니다.");
            }
        }
        message.setAttachments(binaryContents);
    }

    public boolean isAuthor(UUID messageId, UUID userId){
        return messageRepository.existsByIdAndAuthor_Id(messageId, userId);
    }
}
