package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.sse.SseService;
import com.sprint.mission.discodeit.sse.dto.SseBroadcastMessageEvent;
import com.sprint.mission.discodeit.sse.dto.SseMulticastMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseMessageEventListener {
    private final SseService sseService;

    @Async
    @TransactionalEventListener
    public void no(SseBroadcastMessageEvent event){

        sseService.broadcast(event.name(),event.date());
    }

    @Async
    @TransactionalEventListener
    public void on(SseMulticastMessageEvent event){

        sseService.send(event.receiverIds(),event.name(),event.date());
    }

}
