package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // *저장로직* 메시지 보내기 (생성)
    Message sendMessage(UUID authorId, UUID channelId, String authorName,
                        String channelName, String content);

    // *저장로직* 특정 유저의 모든 메시지 조회
    List<Message> findMessagesByUserId(UUID userId);

    // *저장로직* 특정 채널의 모든 메시지 조회
    List<Message> findMessagesByChannelId(UUID channelId);

    // *저장로직* 메시지 내용 수정
    boolean updateContent(Message message, String updatedContent);

    // *비즈니스로직* 유저명 변경 시 유저 메시지 작성자 이름 전부 변경
    void modifyAuthorName(UUID authorId, String updatedAuthorName);

    // *비즈니스로직* 채널명 변경 시 채널 메시지 채널명 전부 변경
    void modifyChannelName(UUID channelId, String updatedChannelName);

    // *비즈니스로직* 유저가 삭제되면 해당 유저 메시지 작성자 이름 전부 (알 수 없음) 으로 수정
    void anonymizeAuthorName(UUID authorId);

    // *저장로직* 해당 채널 소속 메시지 전부 삭제
    void deleteAllMessagesInChannel(UUID channelId);

    // *저장로직* 메시지 삭제
    boolean deleteMessage(Message message);

    boolean notExist(Message message);
}
