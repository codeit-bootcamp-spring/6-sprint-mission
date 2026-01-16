package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentUploader;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentUploader implements BinaryContentUploader {

  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;

  public BinaryContent uploadBinaryContent(String fileName, byte[] bytes, String contentType) {
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContent saved = binaryContentRepository.save(binaryContent);

    log.info("사진 업로드: {}", saved.getId());

    eventPublisher.publishEvent(BinaryContentCreatedEvent.builder()
        .binaryContentId(saved.getId())
        .file(bytes)
        .build());

    return saved;
  }

  public BinaryContent uploadBinaryContent(MultipartFile profile) {
    BinaryContent binaryContent = Optional.ofNullable(profile)
        .filter(file -> !file.isEmpty())
        .map(file ->
            new BinaryContent(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType())
        ).orElse(null);

    if (binaryContent != null) {
      BinaryContent saved = binaryContentRepository.save(binaryContent);

      log.info("사진 업로드: {}", saved.getId());

      try {
        eventPublisher.publishEvent(BinaryContentCreatedEvent.builder()
            .binaryContentId(saved.getId())
            .file(profile.getBytes())
            .build());

        return saved;

      } catch (IOException e) {
        log.error("사진 업로드 실패", e);
        throw new RuntimeException("사진 업로드 실패");
      }
    }
    return null;
  }
}
