package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.handler.StoragePutHandler;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageEventListener {

  private final StoragePutHandler storagePutHandler;

  /*
   * 비동기 환경은 새로운 스레드이므로 트랜잭션 x
   * 메인 스레드 작업인 메타 데이터 저장 후 리스너 작업 수행
   * discodeit.storage.type에 따라 S3 또는 로컬 스토리지에 파일 저장
   */
  @Timed("async.storage.put")
  @Async("binaryContentEventTaskExecutor")
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 500, multiplier = 2.0)
  )
  @TransactionalEventListener
  public void handleStoragePut(BinaryContentCreatedEvent event) {

    log.debug("비동기 파일 저장 시작: binaryContentId={}", event.binaryContentId());

    storagePutHandler.putSuccess(event);

    log.debug("비동기 파일 저장 완료: binaryContentId={}", event.binaryContentId());
  }

  @Recover
  public void recoverStoragePut(Exception e, BinaryContentCreatedEvent event) {

    storagePutHandler.putFailAndPublishEvent(e, event);

    throw new RuntimeException("파일 저장에 실패하였습니다. binaryContentId=" + event.binaryContentId(), e);
  }
}
