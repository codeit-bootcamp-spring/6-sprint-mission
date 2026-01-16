package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDTO;
import com.sprint.mission.discodeit.dto.api.request.MessageRequestDTO;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/pub/messages")
  public void sendMessage(@Valid MessageRequestDTO.MessageCreateRequest messageCreateRequest) {

    MessageDTO.CreateMessageCommand createMessageCommand = MessageDTO.CreateMessageCommand.builder()
        .content(messageCreateRequest.content())
        .channelId(messageCreateRequest.channelId())
        .userId(messageCreateRequest.authorId())
        .build();

    messageService.createMessage(createMessageCommand);

  }

}
