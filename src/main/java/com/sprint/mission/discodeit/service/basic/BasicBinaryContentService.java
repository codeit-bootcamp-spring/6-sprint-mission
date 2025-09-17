package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public void registerProfile(UUID userId, String filename) {
        Optional<BinaryContent> existingContent = binaryContentRepository.findByUserId(userId);

        if (existingContent.isPresent()) {
            BinaryContent binaryContent = existingContent.get();
            binaryContent.setFilename(filename);
            binaryContentRepository.save(binaryContent);
        } else {

            BinaryContent newContent = new BinaryContent(userId, "image/jpeg", filename);
            binaryContentRepository.save(newContent);
        }
    }

    @Override
    public void deleteProfile(UUID userId) {
        Optional<BinaryContent> existingContent = binaryContentRepository.findByUserId(userId);

        if (existingContent.isPresent()) {
            binaryContentRepository.deleteByUserId(userId);
        }
    }
}