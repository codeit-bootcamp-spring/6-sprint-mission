package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import com.sprint.mission.discodeit.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicAttachmentService implements AttachmentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public void saveAttachments(UUID messageId, List<BinaryContentDto> attachments) {
        List<BinaryContent> binaryContents = new ArrayList<>();
        for (BinaryContentDto dto : attachments) {
            BinaryContent newContent = new BinaryContent(dto.userId(), dto.contentType(), dto.filename());
            binaryContents.add(newContent);
        }

        binaryContentRepository.saveAll(binaryContents);
    }

    @Override
    public List<BinaryContentDto> findAllByMessageId(UUID messageId) {
        Optional<BinaryContent> contents = binaryContentRepository.findByMessageId(messageId);
        return contents.stream()
                .map(content -> new BinaryContentDto(content.getUserId(), content.getContentType(), content.getFilename(), content.getData()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BinaryContentDto> findByMessageIdAndFilename(UUID messageId, String filename) {
        Optional<BinaryContent> contents = binaryContentRepository.findByMessageId(messageId);
        return Optional.ofNullable(contents.stream()
                .filter(content -> content.getFilename().equals(filename))
                .map(content -> new BinaryContentDto(content.getUserId(), content.getContentType(), content.getFilename(), content.getData()))
                .findFirst()
                .orElse(null));
    }
}