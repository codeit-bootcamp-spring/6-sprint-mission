package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StorageTxHandler {

  private final BinaryContentRepository binaryContentRepository;

  @Transactional
  public void updateStorageStatus(UUID binaryContentId, boolean isSuccess) {

    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(BinaryContentNotFoundException::new);

    if (isSuccess) {
      binaryContent.success();
    } else {
      binaryContent.fail();
    }
    binaryContentRepository.save(binaryContent);
  }
}
