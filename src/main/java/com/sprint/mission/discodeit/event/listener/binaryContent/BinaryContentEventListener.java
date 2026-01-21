package com.sprint.mission.discodeit.event.listener.binaryContent;

//import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
//import com.sprint.mission.discodeit.entity.BinaryContentStatus;
//import com.sprint.mission.discodeit.event.event.BinaryContentCreatedEvent;
//import com.sprint.mission.discodeit.event.event.FileUploadFailedEvent;
//import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
//import com.sprint.mission.discodeit.service.event.EventBinaryContentService;
//import com.sprint.mission.discodeit.storage.BinaryContentStorage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Recover;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class BinaryContentEventListener {
//    private final EventBinaryContentService eventBinaryContentService;
//    private final ApplicationEventPublisher applicationEventPublisher;
//
//    @Async("eventTaskExecutor")
//    @Retryable(
//            retryFor = Exception.class,
//            noRetryFor = BinaryContentNotFoundException.class,
//            maxAttempts = 3,
//            backoff = @Backoff(
//                    delay = 1000,
//                    maxDelay = 5000,
//                    multiplier = 2.0
//            ),
//            label = "BinaryContentCreatedEvent",
//            recover = "binaryContentCreateEventRecover"
//    )
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
//        BinaryContentStorage storage = event.getBinaryContentStorage();
//        UUID binaryContentId = event.getBinaryContentId();
//        byte[] bytes = event.getBytes();
//
//        storage.put(binaryContentId, bytes);
//        eventBinaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.SUCCESS);
//        log.info("[{}] BinaryContent create success", Thread.currentThread().getName());
//    }
//
//    @Recover
//    public void binaryContentCreateEventRecover(Exception ex, BinaryContentCreatedEvent event) {
//        String requestId = MDC.get(MDCLoggingInterceptor.REQUEST_ID);
//        String binaryContentId = event.getBinaryContentId().toString();
//        String error;
//        if (ex instanceof BinaryContentNotFoundException) {
//            log.error("[{}] BinaryContent not found", Thread.currentThread().getName());
//            error = "BinaryContent를 찾을 수 없습니다.";
//        } else {
//            log.error("[{}] BinaryContent Storage exception.", Thread.currentThread().getName());
//            error = ex.getMessage();
//        }
//
//        applicationEventPublisher.publishEvent(
//                new FileUploadFailedEvent(
//                        requestId, binaryContentId, error
//                )
//        );
//    }
//}