package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.repository.MessageRepositoryInterface;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepositoryInterface messageRepository;

    // 생성자 - 생성 시 레포지토리 주입
    public BasicMessageService(MessageRepositoryInterface messageRepository) {
        this.messageRepository = messageRepository;
    }

    // 메시지 생성
    @Override
    public Message sendMessage(UUID authorId, UUID channelId, String authorName, String channelName, String content) {
        return messageRepository.sendMessage(authorId, channelId, authorName, channelName, content);
    }

    // 특정 유저의 모든 메시지 조회
    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return messageRepository.findMessagesByUserId(userId);
    }

    // 특정 채널의 모든 메시지 조회
    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return messageRepository.findMessagesByChannelId(channelId);
    }

    // 메시지 내용 수정
    @Override
    public boolean updateContent(Message message, String updatedContent) {
        if(notExist(message)){ return false;}
        messageRepository.updateContent(message, updatedContent);
        return true;
    }

    // 유저명 변경 시 유저 메시지 작성자 이름 전부 변경
    @Override
    public void modifyAuthorName(UUID authorId, String updatedAuthorName) {
        List<Message> messages = messageRepository.findMessagesByUserId(authorId);
        messages.forEach(m -> m.updateAuthorName(updatedAuthorName));
        messageRepository.saveData();
    }

    // 채널명 변경 시 채널 메시지 채널명 전부 변경
    @Override
    public void modifyChannelName(UUID channelId, String updatedChannelName) {
        List<Message> messages = messageRepository.findMessagesByChannelId(channelId);
        messages.forEach(m -> m.updateChannelName(updatedChannelName));
        messageRepository.saveData();
    }

    // 유저 삭제 시 해당 유저 메시지 작성자명 전부 (알 수 없음) 으로 변경
    @Override
    public void anonymizeAuthorName(UUID authorId) {
        modifyAuthorName(authorId, "(알 수 없음)");
    }

    // 해당 채널 소속 메시지 전부 삭제
    @Override
    public void deleteAllMessagesInChannel(UUID channelId) {
        messageRepository.deleteAllMessagesInChannel(channelId);
    }

    // 메시지 삭제
    @Override
    public boolean deleteMessage(Message message) {
        if(notExist(message)){ return false;}
        messageRepository.deleteMessage(message);
        return true;
    }

    @Override
    public boolean notExist(Message message) {
        return messageRepository.notExist(message);
    }
}
