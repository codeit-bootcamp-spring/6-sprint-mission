package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final ApplicationEventPublisher applicationEventPublisher;

    @MessageMapping("/pub/messages")
    public void pubMessage(@Payload MessageCreateRequest request) {

        applicationEventPublisher.publishEvent(request);
    }

    
}
