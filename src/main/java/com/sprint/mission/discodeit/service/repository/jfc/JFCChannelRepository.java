package com.sprint.mission.discodeit.service.repository.jfc;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.repository.ChannelRepositoryInterface;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JFCChannelRepository implements ChannelRepositoryInterface {
    private Map<UUID, Channel> data;

    // 생성자
    public JFCChannelRepository(){ this.data = new HashMap<>(); }

    // 채널 생성
    @Override
    public Channel createChannel(String name, String information){
        Channel newChannel = new Channel(name, information);
        data.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    // 채널 아이디로 특정 채널 조회
    @Override
    public Channel findById(UUID channelId){
        if(!data.containsKey(channelId)){ // 존재하지 않을 시 null 반환
            return null;
        }
        return data.get(channelId); // 존재할 시 channel 객체 참조값 반환
    }
    // 전체 채널 조회
    @Override
    public List<Channel> findAll(){
        return data.values().stream().toList(); // 아무 채널도 존재하지 않을 시 빈 list 반환
    }

    @Override
    public void updateName(Channel channel, String updatedName) {
        channel.updateName(updatedName);
    }

    // 채널 정보 수정
    @Override
    public void updateInformation(Channel channel, String updateInformation){
        channel.updateInformation(updateInformation);
    }

    // 채널 삭제
    public void deleteChannel(Channel channel){
        data.remove(channel.getId());
    }

    // 인터페이스에 의해 구현한 깡통 메서드 (file~repository와 호환되기 위해...)
    public void saveData(){ data = data; }

}
