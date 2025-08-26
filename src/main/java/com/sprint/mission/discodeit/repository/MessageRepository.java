package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.Message ;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // 메시지 송신자 User ID를 위해 필요


public interface MessageRepository {
    Message createMessage(Message message);
    Optional<Message> readMessage(Long Id);
    List<Message> readAllMessages();
    List<Message> readMessagesByChannelId(Long channelId); // 특정 채널의 메시지 조회
    List<Message> readMessagesBySenderUserId(UUID senderUserId); // 특정 사용자(UUID)의 메시지 조회
    Optional<Message> updateMessage(Message message);
    boolean deleteMessage(Long Id);
}
