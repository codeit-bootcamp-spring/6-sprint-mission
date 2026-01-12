package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.Message.MessageCreatedEvent;
import com.sprint.mission.discodeit.dto.User.RoleUpdatedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
    private NotificationService notificationService;

    private final String ROLE_UPDATE_TITLE = "권한이 변경되었습니다.";

    @Async
    @TransactionalEventListener
    public void on(MessageCreatedEvent event){
        String title = event.userName()+" (#"+event.channelName()+")";
        notificationService.create(event.userId(),title,event.content());
    }

    @Async
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event){
        String content = event.oldRole() +" -> "+event.newRole();
        notificationService.create(event.userId(),ROLE_UPDATE_TITLE,content);
    }

}
