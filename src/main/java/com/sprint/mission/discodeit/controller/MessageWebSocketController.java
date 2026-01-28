package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")
  public void sendMessage(@Payload CreateMessageRequest request) {
    messageService.create(request, Collections.emptyList());
  }
}
