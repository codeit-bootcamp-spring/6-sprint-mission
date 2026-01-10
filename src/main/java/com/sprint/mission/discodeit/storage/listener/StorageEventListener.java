package com.sprint.mission.discodeit.storage.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.event.BinaryContentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  /*
   * 트랜잭션 커밋 (메타 데이터 저장 성공) 후에 이벤트 처리
   * db 저장 위해서 새로운 트랜잭션 생성
   * discodeit.storage.type에 따라 S3 또는 로컬 스토리지에 파일 저장
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleStoragePutEvent(BinaryContentCreatedEvent event) {

    BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
        .orElseThrow(BinaryContentNotFoundException::new);
    try {
      log.debug("파일 저장 시작: binaryContentId={}, fileName={}",
          event.binaryContentId(), binaryContent.getFileName());
      binaryContentStorage.put(event.binaryContentId(), event.file());
      binaryContent.success();
      binaryContentRepository.save(binaryContent);
    } catch (Exception e) {
      log.debug("파일 저장 실패: binaryContentId={}, fileName={}",
          event.binaryContentId(), binaryContent.getFileName(), e);
      binaryContent.fail();
      binaryContentRepository.save(binaryContent);
      throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
    }
  }
}
