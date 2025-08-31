package com.sprint.mission.discodeit.service.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.repository.MessageRepositoryInterface;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepositoryInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Map<UUID, Message> data;

    // 생성자 - message.ser 파일에서 로드
    public FileMessageRepository(){ data = loadData();}

    // 메시지 보내기 (생성)
    @Override
    public Message sendMessage(UUID authorId, UUID channelId, String authorName,
                               String channelName, String content){
        Message newMessage = new Message(authorId, channelId, authorName, channelName, content);
        data.put(newMessage.getId(), newMessage);
        saveData();
        return newMessage; // 생성한 메시지 객체 참조값 반환
    }

    // 특정 유저의 모든 메시지 조회
    @Override
    public List<Message> findMessagesByUserId(UUID userId){
        return data.values().stream().filter(m -> m.getAuthorId().equals(userId))
                .collect(Collectors.toList());
        // 아무 메시지도 존재하지 않을 시 빈 List 반환
    }

    // 특정 채널의 모든 메시지 조회
    @Override
    public List<Message> findMessagesByChannelId(UUID channelId){
        return data.values().stream().filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
        // 아무 메시지도 존재하지 않을 시 빈 List 반환
    }

    // 메시지 내용 수정
    @Override
    public void updateContent(Message message, String updatedContent){
        message.updateContent(updatedContent);
        saveData();
    }



    // 해당 채널 소속 메시지 전부 삭제
    @Override
    public void deleteAllMessagesInChannel(UUID channelId){
        data.values().removeIf(m -> m.getChannelId().equals(channelId));
        saveData();
    }

    // 메시지 삭제
    @Override
    public void deleteMessage(Message message){
        data.remove(message.getId());
        saveData();
    }

    // 데이터 로드
    public Map<UUID, Message> loadData(){
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream
                (new FileInputStream("message.ser")))) {
            return (Map<UUID, Message>) ois.readObject(); // 읽어온 역직렬화된 맵 덮어쓰기
        } catch (FileNotFoundException e) { // 읽어올 파일 존재X (처음 실행되는 경우)
            Map<UUID, Message> newData = new HashMap<>(); // 새 맵 생성해서 반환
            return newData;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[Error] 메시지 데이터 로딩 실패", e);
        }
    }

    // 데이터 덮어쓰기 (저장)
    public void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream("message.ser")))){
            oos.writeObject(data); // 기존 파일 덮어쓰기
        } catch (IOException e) {
            throw new RuntimeException("[Error] 메시지 데이터 저장 실패", e);
        }
    }



    // 메시지 존재 여부
    @Override
    public boolean notExist(Message message){
        return !data.containsKey(message.getId());
    }
}
