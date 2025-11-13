package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.AttachmentService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicAttachmentService implements AttachmentService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    @Transactional
    public void saveAttachments(UUID messageId, List<BinaryContentDto> attachments) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));

        List<BinaryContent> newAttachments = attachments.stream()
                .map(this::toEntity)
                .toList();

        newAttachments.forEach(message::addAttachment);
    }

    @Override
    public List<BinaryContentDto> findAllByMessageId(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));

        return message.getAttachments().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Optional<BinaryContentDto> findByMessageIdAndFilename(UUID messageId, String filename) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));

        return message.getAttachments().stream()
                .filter(attachment -> attachment.getFileName().equals(filename))
                .findFirst()
                .map(this::toDto);
    }

    private BinaryContent toEntity(BinaryContentDto dto) {
        return new BinaryContent(
                dto.filename(),
                dto.size(),
                dto.contentType()
        );
    }

    private BinaryContentDto toDto(BinaryContent entity) {
        return new BinaryContentDto(
                entity.getId(),
                entity.getContentType(),
                entity.getFileName(),
                entity.getSize(),
                null
        );
    }
}