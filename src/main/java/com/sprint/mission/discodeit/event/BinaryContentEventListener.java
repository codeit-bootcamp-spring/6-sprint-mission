package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.enumtype.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.file.FileOutPutException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {
    private final LocalBinaryContentStorage contentStorage;
    private final BinaryContentService contentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void binaryContentPut(BinaryContentCreatedEvent event){
        try {
            UUID binaryContent = contentStorage.put(event);
            contentService.updateStatus(binaryContent, BinaryContentStatus.SUCCESS);
        }catch (IOException e) {
            log.error("파일 저장 오류 발생: 파일Id={}",event.id());
            contentService.updateStatus(event.id(),BinaryContentStatus.FAIL);
            throw new FileOutPutException();
        }
    }

}
