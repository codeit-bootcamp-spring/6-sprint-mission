package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.BinaryContentDTO.BinaryContentCreateCommand;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import com.sprint.mission.discodeit.event.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.NoSuchBinaryContentException;
import com.sprint.mission.discodeit.mapper.BinaryContentEntityMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentEntityMapper binaryContentEntityMapper;

  @Transactional
  @Override
  public BinaryContentDTO.BinaryContent createBinaryContent(BinaryContentCreateCommand request) {

    BinaryContentEntity binaryContentEntity = BinaryContentEntity.builder()
        .fileName(request.fileName())
        .size((long) request.data().length)
        .contentType(request.contentType())
        .build();

    binaryContentEntity = binaryContentRepository.save(binaryContentEntity);
    eventPublisher.publishEvent(
        BinaryContentCreatedEvent.of(binaryContentEntity.getId(), request.data())
    );

    log.debug("Creating binary content with id {}", binaryContentEntity.getId());

    return binaryContentEntityMapper.toBinaryContent(binaryContentEntity);

  }

  @Override
  public boolean existBinaryContentById(UUID id) {
    return binaryContentRepository.existsById(id);
  }

  @Override
  public BinaryContentDTO.BinaryContent findBinaryContentById(UUID id) {

    BinaryContentEntity binaryContentEntity = binaryContentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Could not find binary content with id {}", id);
          throw new NoSuchBinaryContentException();
        });

    byte[] bytes = null;

    try (InputStream is = binaryContentStorage.get(binaryContentEntity.getId())) {
      bytes = is.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return BinaryContentDTO.BinaryContent.builder()
        .id(binaryContentEntity.getId())
        .fileName(binaryContentEntity.getFileName())
        .size(binaryContentEntity.getSize())
        .createdAt(binaryContentEntity.getCreatedAt())
        .contentType(binaryContentEntity.getContentType())
        .bytes(bytes)
        .build();
  }

  @Override
  public List<BinaryContentDTO.BinaryContent> findAllBinaryContentByIdIn(List<UUID> uuidList) {
    return binaryContentRepository.findAllByIdIn(uuidList).stream()
        .map(binaryContent -> {
          try {
            return BinaryContentDTO.BinaryContent.builder()
                .id(binaryContent.getId())
                .fileName(binaryContent.getFileName())
                .size(binaryContent.getSize())
                .createdAt(binaryContent.getCreatedAt())
                .contentType(binaryContent.getContentType())
                .bytes(binaryContentStorage.get(binaryContent.getId()).readAllBytes())
                .build();
          } catch (IOException e) {
            log.warn("Could not find binary content with id {}", binaryContent.getId());
            throw new NoSuchBinaryContentException();
          }
        })
        .toList();
  }

  @Override
  public List<BinaryContentDTO.BinaryContent> findAllBinaryContent() {
    return binaryContentRepository.findAll().stream()
        .map(binaryContent -> {
          try {
            return BinaryContentDTO.BinaryContent.builder()
                .id(binaryContent.getId())
                .fileName(binaryContent.getFileName())
                .size(binaryContent.getSize())
                .createdAt(binaryContent.getCreatedAt())
                .contentType(binaryContent.getContentType())
                .bytes(binaryContentStorage.get(binaryContent.getId()).readAllBytes())
                .build();
          } catch (IOException e) {
            log.warn("Could not find binary content with id {}", binaryContent.getId());
            throw new NoSuchBinaryContentException();
          }
        })
        .toList();
  }

  @Override
  public void updateBinaryContentStatus(UUID id, BinaryContentStatus status) {

    BinaryContentEntity binaryContentEntity = binaryContentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Could not find binary content with id {}", id);
          throw new NoSuchBinaryContentException();
        });

    binaryContentEntity.updateStatus(status);

    log.info("Updated binary content status with id {} to {}", id, status);

  }

  @Transactional
  @Override
  public void deleteBinaryContentById(UUID id) {

    if (!binaryContentRepository.existsById(id)) {
      log.warn("Could not find binary content with id {}", id);
      throw new NoSuchBinaryContentException();
    }

    binaryContentRepository.deleteById(id);
    binaryContentStorage.deleteById(id);

    log.debug("Deleted binary content with id {}", id);

  }
}
