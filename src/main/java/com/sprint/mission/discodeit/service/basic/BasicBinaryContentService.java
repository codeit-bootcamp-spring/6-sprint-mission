package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.model.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
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
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public BinaryContent create(CreateBinaryContentRequest request) {
    log.debug("바이너리 컨텐츠 생성 시작: fileName={}, size={}, contentType={}",
        request.fileName(), request.bytes().length, request.contentType());

    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContent saved = binaryContentRepository.save(binaryContent);
    eventPublisher.publishEvent(new BinaryContentCreatedEvent(saved.getId(), request.bytes()));

    log.info("바이너리 컨텐츠 생성 완료: id={}, fileName={}, size={}",
        binaryContent.getId(), fileName, bytes.length);
    return saved;
  }

  @Override
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = find(binaryContentId);
    binaryContent.setStatus(status);
    binaryContentRepository.save(binaryContent);
    return new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContent find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> {
          log.warn("BinaryContent Not Found. binaryContentId: {}", binaryContentId);
          return new BinaryContentNotFoundException();
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIdList) {
    return binaryContentRepository.findAllByIdIn((binaryContentIdList));
  }

  @Override
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.warn("BinaryContent Not Found. binaryContentId: {}", binaryContentId);
      throw new BinaryContentNotFoundException();
    }
    binaryContentRepository.deleteById(binaryContentId);
  }
}
