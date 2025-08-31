package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {
    private Map<UUID, Channel> data;
    private FileUserChannelService fileUserChannelService = new FileUserChannelService();
    private FileMessageService fileMessageService = new FileMessageService();

    // 생성자
    public FileChannelService() { data = loadData(); }

    // *저장로직* 채널 생성
    @Override
    public Channel createChannel(String name, String information){
        Channel newChannel = new Channel(name, information);
        data.put(newChannel.getId(), newChannel);
        saveData();
        return newChannel;
    }

    // *저장로직* 전체 채널 조회
    @Override
    public List<Channel> findAll(){
        return data.values().stream().toList(); // 아무 채널도 존재하지 않을 시 빈 list 반환
    }
    // *비즈니스로직* 특정 유저의 모든 채널 조회
    @Override
    public List<Channel> findAllChannelsByUserId(UUID userId){
        List<UUID> channels = fileUserChannelService.findChannelListOfUserId(userId);
        return channels.stream().map(u->data.get(u)).collect(Collectors.toList());
        // 아무 채널도 존재하지 않을 시 빈 Channel list 반환
    }

    // *비즈니스로직* 채널 이름 수정
    @Override
    public boolean updateName(Channel channel, String updatedName){
        if(notExist(channel)){ return false; } // 채널 존재하지 않을 시 return false
        // 존재할 시
        channel.updateName(updatedName); // 채널 이름 업데이트
        fileMessageService.modifyChannelName(channel.getId(), updatedName); // 채널 메시지 필드 채널명 업데이트
        saveData(); // 모든 연산이 성공한 이후 덮어쓰기
        return true;
    }
    // *저장로직* 채널 정보 수정
    @Override
    public boolean updateInformation(Channel channel, String updateInformation){
        if(notExist(channel)){ return false; } // 채널 존재하지 않을 시 return false
        channel.updateInformation(updateInformation); // 존재할 시 update & return true
        saveData();
        return true;
    }

    // *비즈니스로직* 채널 삭제
    @Override
    public boolean deleteChannel(Channel channel){
        if(notExist(channel)){ return false; } // 채널 존재하지 않을 시 return false
        //채널 존재 시
        fileMessageService.deleteAllMessagesInChannel(channel.getId()); // 채널 안 메시지 모두 삭제
        fileUserChannelService.removeAllOfChannel(channel.getId()); // 해당 채널의 유저-채널 관계 삭제
        data.remove(channel.getId()); // 채널 삭제하고 return true
        saveData();
        return true;
    }


    // *저장로직* 데이터 로드
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

    // *저장로직* 데이터 덮어쓰기 (저장)
    public void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream("channel.ser")))){
            oos.writeObject(data); // 기존 파일 덮어쓰기
        } catch (IOException e) {
            throw new RuntimeException("[Error] 채널 데이터 저장 실패", e);
        }
    }

    // *저장로직* 채널 존재 여부 확인
    public boolean notExist(Channel channel){
        return !data.containsKey(channel.getId());
    }

}
