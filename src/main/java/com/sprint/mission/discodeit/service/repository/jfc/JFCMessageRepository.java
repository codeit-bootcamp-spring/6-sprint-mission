package com.sprint.mission.discodeit.service.repository.jfc;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.repository.MessageRepositoryInterface;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JFCMessageRepository implements MessageRepositoryInterface {
    private Map<UUID, Message> data;

    // 생성자 - 해시맵 생성
    public JFCMessageRepository(){ data = new HashMap<>();}

    // 메시지 보내기 (생성)
    public Message sendMessage(UUID authorId, UUID channelId, String authorName,
                               String channelName, String content){
        Message newMessage = new Message(authorId, channelId, authorName, channelName, content);
        data.put(newMessage.getId(), newMessage);
        return newMessage; // 생성한 메시지 객체 참조값 반환
    }

    // 특정 유저의 모든 메시지 조회
    public List<Message> findMessagesByUserId(UUID userId){
        return data.values().stream().filter(m -> m.getAuthorId().equals(userId))
                .collect(Collectors.toList());
        // 아무 메시지도 존재하지 않을 시 빈 List 반환
    }

    // 특정 채널의 모든 메시지 조회
    public List<Message> findMessagesByChannelId(UUID channelId){
        return data.values().stream().filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
        // 아무 메시지도 존재하지 않을 시 빈 List 반환
    }

    // 메시지 내용 수정
    public void updateContent(Message message, String updatedContent){
        message.updateContent(updatedContent); // 존재할 시 update & return true
    }

    // 해당 채널 소속 메시지 전부 삭제
    public void deleteAllMessagesInChannel(UUID channelId){
        data.values().removeIf(m -> m.getChannelId().equals(channelId));
    }

    // 메시지 삭제
    public void deleteMessage(Message message){
        data.remove(message.getId());
    }


    // 메시지 존재 여부
    public boolean notExist(Message message){
        return !data.containsKey(message.getId());
    }

    // 인터페이스에 의해 구현한 깡통 메서드 (file~repository와 호환되기 위해...)
    public void saveData(){ data = data; }
}
