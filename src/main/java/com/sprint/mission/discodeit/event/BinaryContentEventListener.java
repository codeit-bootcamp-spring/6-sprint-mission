package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.enumtype.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.file.FileOutPutException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService contentService;
    private final NotificationService notificationService;

    //요구사항에는 따로 binaryContent인벤트 리스너를 만들어라고 말이 없어지만 만든 이유는
    // 1. 파일을 저장하는 클레스에 다는 역할을 안줄려고 했습니다.
    // 2. 로컬과 S3는 같은 스토리지 인터페이스를 공유 하고 있어 이를 최대한 이용해보고자 하나의 함수로 만들었습니다.

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void binaryContentPut(BinaryContentCreatedEvent event){
        try {
            binaryContentStorage.put(event)
                    .thenAccept(uuid -> {
                        contentService.updateStatus(uuid,BinaryContentStatus.SUCCESS);
                    }).exceptionally(ex ->{
                        log.error("파일 저장중 오류 발생: 파일Id={}, 사유={}", event.id(), ex.getMessage());
                        contentService.updateStatus(event.id(), BinaryContentStatus.FAIL);
                        if(ex.getCause() instanceof DiscodeitException e){
                            String content = errContent(event.id(), e.getMessage());
                            notificationService.createToAdmins(e.getErrorCode().getMessage(),content);
                        }
                        return null;
                    });
        }catch (IOException e){
            log.error("파일 저장요청 오류 발생(Sync Error): 파일Id={}, 사유={}", event.id(), e.getMessage());
            contentService.updateStatus(event.id(), BinaryContentStatus.FAIL);
            String content = errContent(event.id(), e.getMessage());
            notificationService.createToAdmins(ErrorCode.FILE_OUT_PUT_FAIL.getMessage(), content);

        }
    }

    private String errContent(UUID binaryContentId,String errorMessage){
        String requestId = MDC.get("requestId");
        return String.format(
                "RequestId: %s\nBinaryContentId: %s\nError: %s",
                requestId.isEmpty() ? null : requestId,
                binaryContentId,
                errorMessage
        );

    }
}
