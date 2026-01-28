package com.sprint.mission.discodeit.events.listener;

import com.sprint.mission.discodeit.events.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListner {

    private final SimpMessageSendingOperations messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        messagingTemplate.convertAndSend("/sub/channels." + event.getChannel().getId() + ".messages", event.getContent());
    }
}
