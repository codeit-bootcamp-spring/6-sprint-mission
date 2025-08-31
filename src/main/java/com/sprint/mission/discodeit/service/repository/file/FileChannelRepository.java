package com.sprint.mission.discodeit.service.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.repository.ChannelRepositoryInterface;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepositoryInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Map<UUID, Channel> data;

    // 생성자
    public FileChannelRepository(){ data = loadData(); }

    // 채널 생성
    @Override
    public Channel createChannel(String name, String information){
        Channel newChannel = new Channel(name, information);
        data.put(newChannel.getId(), newChannel);
        saveData();
        return newChannel;
    }

    // 채널 아이디로 특정 채널 조회
    @Override
    public Channel findById(UUID channelId){
        return data.get(channelId);
    }
    // 전체 채널 조회
    @Override
    public List<Channel> findAll(){
        return data.values().stream().toList(); // 아무 채널도 존재하지 않을 시 빈 list 반환
    }

    public void updateName(Channel channel, String updatedName){
        channel.updateName(updatedName);
        saveData();
    }

    // 채널 정보 수정
    @Override
    public void updateInformation(Channel channel, String updateInformation){
        channel.updateInformation(updateInformation);
        saveData();
    }

    // 데이터 로드
    public Map<UUID, Channel> loadData(){
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream
                (new FileInputStream("channel.ser")))) {
            return (Map<UUID, Channel>) ois.readObject(); // 읽어온 역직렬화된 맵 덮어쓰기
        } catch (FileNotFoundException e) { // 읽어올 파일 존재X (처음 실행되는 경우)
            Map<UUID, Channel> newData = new HashMap<>(); // 새 맵 생성해서 반환
            return newData;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[Error] 채널 데이터 로딩 실패", e);
        }
    }

    // 채널 삭제
    @Override
    public void deleteChannel(Channel channel) {
        data.remove(channel.getId());
        saveData();
    }

    // 데이터 덮어쓰기 (저장)
    @Override
    public void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream("channel.ser")))){
            oos.writeObject(data); // 기존 파일 덮어쓰기
        } catch (IOException e) {
            throw new RuntimeException("[Error] 채널 데이터 저장 실패", e);
        }
    }
}
