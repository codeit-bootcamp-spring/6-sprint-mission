package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JFCMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private static final JFCMessageService instance = new JFCMessageService();

    // 생성자 - 싱글톤 패턴 (Message data의 유일성 보장)
    private JFCMessageService(){
        data = new HashMap<>();
    }
    // GetInstance
    public static JFCMessageService getInstance() {
        return instance;
    }

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
    public boolean updateContent(Message message, String updatedContent){
        if(notExist(message)){ return false; } // 메시지 존재하지 않을 시 return false
        message.updateContent(updatedContent); // 존재할 시 update & return true
        return true;
    }
    // 유저명 변경 시 유저 메시지 작성자 이름 전부 변경
    public void modifyAuthorName(UUID authorId, String updatedAuthorName){
        findMessagesByUserId(authorId).stream()
                .forEach(m -> m.updateAuthorName(updatedAuthorName));
    }
    // 채널명 변경 시 채널 메시지 채널명 전부 변경
    public void modifyChannelName(UUID channelId, String updatedChannelName){
        findMessagesByChannelId(channelId).stream()
                .forEach(m -> m.updateChannelName(updatedChannelName));
    }
    // 유저가 삭제되면 해당 유저 메시지 작성자 이름 전부 (알 수 없음) 으로 수정
    public void anonymizeAuthorName(UUID authorId){
        modifyAuthorName(authorId, "(알 수 없음)");
    }

    // 해당 채널 소속 메시지 전부 삭제
    public void deleteAllMessagesInChannel(UUID channelId){
        data.values().removeIf(m -> m.getChannelId().equals(channelId));
    }

    // 메시지 삭제
    public boolean deleteMessage(Message message){
        if(notExist(message)){ return false; } // 메시지 존재하지 않을 시 return false
        data.remove(message.getId()); // 메시지 존재 시 삭제 후 return true
        return true;
    }


    // 메시지 존재 여부
    public boolean notExist(Message message){
        return !data.containsKey(message.getId());
    }

}
