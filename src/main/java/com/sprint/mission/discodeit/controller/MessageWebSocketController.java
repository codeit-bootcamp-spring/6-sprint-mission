package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * 첨부파일 없는 메세지를 전송 (미션12)
 */
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, MessageResponseDto> kafkaTemplate;

    @MessageMapping("/pub/messages")
    public void sendMessage(@Payload MessageCreateRequestDto request) {
        MessageResponseDto responseDto = messageService.create(request, null);

        kafkaTemplate.send("chat-events", responseDto);
//        String destination = String.format("/sub/channels.%s.messages", request.channelId());
//        messagingTemplate.convertAndSend(destination, responseDto);
    }

}
