package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageKafkaConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    // 서버 인스턴스마다 고유한 그룹 아이디 생성
    @KafkaListener(
            topics = "chat-events",
            groupId = "chat-group-#{T(java.util.UUID).randomUUID().toString()}"
    )
    public void consume(MessageResponseDto message) {
        // 메시지를 받은 각 서버는 자기 서버에 연결된 유저들에게만 배달
        String destination = String.format("/sub/channels.%s.messages", message.channelId());
        messagingTemplate.convertAndSend(destination, message);
    }
}