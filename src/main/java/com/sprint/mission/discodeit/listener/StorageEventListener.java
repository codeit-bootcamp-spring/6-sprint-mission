package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.StoragePutFailedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;

  /*
   * 비동기 환경은 새로운 스레드이므로 트랜잭션 x
   * 새로운 트랜잭션 열어 파일 저장
   * discodeit.storage.type에 따라 S3 또는 로컬 스토리지에 파일 저장
   */
  @Timed("async.storage.put")
  @Async("binaryContentEventTaskExecutor")
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 500, multiplier = 2.0)
  )
  @EventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleStoragePut(BinaryContentCreatedEvent event) {

    log.debug("파일 저장 시작: binaryContentId={}", event.binaryContentId());

    binaryContentStorage.put(event.binaryContentId(), event.file());

    log.debug("파일 저장 성공: binaryContentId={}", event.binaryContentId());

    BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
        .orElseThrow(BinaryContentNotFoundException::new);
    binaryContent.success();
    binaryContentRepository.save(binaryContent);
  }

  @Recover
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recoverStoragePut(Exception e, BinaryContentCreatedEvent event) {

    log.error("파일 저장 재시도 실패: binaryContentId={}, error={}", event.binaryContentId(), e.getMessage());

    BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
        .orElseThrow(BinaryContentNotFoundException::new);
    binaryContent.fail();
    binaryContentRepository.save(binaryContent);

    if (binaryContentStorage instanceof S3BinaryContentStorage) {
      eventPublisher.publishEvent(StoragePutFailedEvent.builder()
          .requestId(MDC.get("requestId"))
          .binaryContentId(event.binaryContentId())
          .errorType(e.getClass().getSimpleName())
          .errorMessage(e.getMessage())
          .build());
    }

    throw new RuntimeException("비동기 파일 저장 최종 실패", e);
  }
}
