package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final MessageService messageService;

    @MessageMapping("/messages")
    public void joinChat(@Payload MessageCreateRequest request) {
        // 만약 호출 하는 곳에서 null을 확인하고 필터를 하면 null 사용해도될까
        // 아니면 빈배열을 보내야하나 아니면 파일 없는 함수를 하나 더만들어야하나?
        messageService.create(null, request);
    }
}