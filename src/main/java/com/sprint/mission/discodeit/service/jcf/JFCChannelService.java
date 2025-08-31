package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JFCChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private static final JFCChannelService instance = new JFCChannelService();
    private final UserChannelService userChannelService = UserChannelService.getInstance();
    private final JFCMessageService messageService = JFCMessageService.getInstance();

    // 생성자 (싱글톤 패턴 - Channel data의 유일성 보장)
    private JFCChannelService(){
        data = new HashMap<>();
    }

    // GetInstance
    public static JFCChannelService getInstance(){
        return instance;
    }


    // 채널 생성
    public Channel createChannel(String name, String information){
        Channel newChannel = new Channel(name, information);
        data.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    // 전체 채널 조회
    public List<Channel> findAll(){
        return data.values().stream().toList(); // 아무 채널도 존재하지 않을 시 빈 list 반환
    }
    // 특정 유저의 모든 채널 조회
    public List<Channel> findAllChannelsByUserId(UUID userId){
        List<UUID> channels = userChannelService.findChannelListOfUserId(userId);
        return channels.stream().map(data::get).collect(Collectors.toList());
        // 아무 채널도 존재하지 않을 시 빈 Channel list 반환
    }


    // 채널 이름 수정
    public boolean updateName(Channel channel, String updatedName){
        if(notExist(channel)){ return false; } // 채널 존재하지 않을 시 return false
        // 존재할 시
        channel.updateName(updatedName); // 채널 이름 업데이트
        messageService.modifyChannelName(channel.getId(), updatedName); // 채널 메시지 필드 채널명 업데이트
        return true;
    }
    // 채널 정보 수정
    public boolean updateInformation(Channel channel, String updateInformation){
        if(notExist(channel)){ return false; } // 채널 존재하지 않을 시 return false
        channel.updateInformation(updateInformation); // 존재할 시 update & return true
        return true;
    }


    // 채널 삭제
    public boolean deleteChannel(Channel channel){
        if(notExist(channel)){ return false; } // 채널 존재하지 않을 시 return false
        //채널 존재 시
        messageService.deleteAllMessagesInChannel(channel.getId()); // 채널 안 메시지 모두 삭제
        userChannelService.removeAllOfChannel(channel.getId()); // 해당 채널의 유저-채널 관계 삭제
        data.remove(channel.getId()); // 채널 삭제하고 return true
        return true;
    }


    // 채널 존재 여부 확인
    public boolean notExist(Channel channel){
        return !data.containsKey(channel.getId());
    }
}
