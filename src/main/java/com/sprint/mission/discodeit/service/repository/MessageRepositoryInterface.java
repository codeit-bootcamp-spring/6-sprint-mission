package com.sprint.mission.discodeit.service.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepositoryInterface {
    // 메시지 보내기 (생성)
    Message sendMessage(UUID authorId, UUID channelId, String authorName,
                        String channelName, String content);

    // 특정 유저의 모든 메시지 조회
    List<Message> findMessagesByUserId(UUID userId);

    // 특정 채널의 모든 메시지 조회
    List<Message> findMessagesByChannelId(UUID channelId);

    // 메시지 내용 수정
    void updateContent(Message message, String updatedContent);

    // 해당 채널 소속 메시지 전부 삭제
    void deleteAllMessagesInChannel(UUID channelId);

    // 메시지 삭제
    void deleteMessage(Message message);

    // 메시지 존재 여부
    boolean notExist(Message message);

    //
    void saveData();
}
