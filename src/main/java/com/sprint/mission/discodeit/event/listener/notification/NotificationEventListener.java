package com.sprint.mission.discodeit.event.listener.notification;

//import com.sprint.mission.discodeit.event.event.NotificationContentFormatter;
//import com.sprint.mission.discodeit.event.event.FileUploadFailedEvent;
//import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
//import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
//import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
//import com.sprint.mission.discodeit.service.event.EventNotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//@RequiredArgsConstructor
//@Slf4j
//@Component
//public class NotificationEventListener {
//    private final EventNotificationService eventNotificationService;
//
//    @Async("eventTaskExecutor")
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleRoleUpdateEvent(RoleUpdatedEvent event) {
//        try {
//            String content = String.format(
//                    NotificationContentFormatter.UPDATED_ROLE.getFormatter(),
//                    event.getBefore().name(),
//                    event.getAfter().name()
//            );
//
//            eventNotificationService.notifyUpdatedRole(event.getReceiverId(), content);
//            log.info("MessageCreatedEvent - Notification Create Success");
//        } catch (UserNotFoundException e) {
//            log.error("MessageCreatedEvent - Receiver Not Found");
//        } catch (Exception e) {
//            log.error("RoleUpdatedEvent - Notification Created Failed");
//        }
//    }
//
//    @Async("eventTaskExecutor")
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleMessageCreatedEvent(MessageCreatedEvent event) {
//        try {
//            String content = String.format(
//                    NotificationContentFormatter.CREATED_MESSAGE.getFormatter(),
//                    event.getSenderUsername(),
//                    event.getContent()
//            );
//
//            eventNotificationService.notifyCreatedMessage(
//                    event.getSenderUsername(),
//                    content,
//                    event.getChannelId()
//            );
//            log.info("MessageCreatedEvent - Notification Create Success");
//        } catch (Exception e) {
//            log.error("MessageCreatedEvent - Notification Created Failed");
//        }
//    }
//
//    @Async("eventTaskExecutor")
//    @EventListener
//    public void handleFileUploadFailedEvent(FileUploadFailedEvent event) {
//        try {
//            String content = String.format(
//                    NotificationContentFormatter.FILE_UPLOAD_FAILED.getFormatter(),
//                    "RequestId", event.getRequestId(),
//                    "BinaryContentId", event.getBinaryContentId(),
//                    "Error", event.getError()
//            );
//
//            eventNotificationService.notifyAdminOfError(content);
//            log.info("FileUploadFailedEvent - Notification Create Success");
//        } catch (UserNotFoundException e) {
//            log.error("FileUploadFailedEvent - Notification Admin Not Found");
//        } catch (Exception e) {
//            log.error("FileUploadFailedEvent - Notification Created Failed");
//        }
//    }
//}
