package com.sprint.mission.discodeit.service.repository.jfc;

import com.sprint.mission.discodeit.service.repository.UserChannelRepositoryInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JFCUserChannelRepository implements UserChannelRepositoryInterface {
    private List<UserChannelRelationship> data;

    // 생성자
    public JFCUserChannelRepository() { data = new ArrayList<>(); }


    // 새 유저-채널 관계 추가
    public boolean put(UUID userId, UUID channelId){
        if(isExist(userId, channelId)){ // 이미 관계 존재
            return false;
        }
        data.add(new UserChannelRelationship(userId, channelId));
        return true;
    }

    // 유저-채널 단일 관계 삭제
    public boolean remove(UUID userId, UUID channelId){
        if(!isExist(userId, channelId)){ // 관계 존재 X
            return false;
        }
        data.removeIf(uc ->
                uc.getUserId().equals(userId) && uc.getChannelId().equals(channelId));
        return true;
    }
    // 특정 유저 모든 관계 삭제
    public void removeAllOfUser(UUID userId){
        data.removeIf(uc -> uc.getUserId().equals(userId));
    }
    // 특정 채널 모든 관계 삭제
    public void removeAllOfChannel(UUID channelId){
        data.removeIf(uc -> uc.getChannelId().equals(channelId));
    }


    // 유저 Id로 속한 채널 list 조회
    public List<UUID> findChannelListOfUserId(UUID userId){
        return data.stream().filter(uc -> uc.getUserId().equals(userId))
                .map(UserChannelRelationship::getChannelId)
                .collect(Collectors.toList());
    }

    // 채널 Id로 속한 유저 list 조회
    public List<UUID> findUserListOfChannelId(UUID channelId){
        return data.stream().filter(uc -> uc.getChannelId().equals(channelId))
                .map(UserChannelRelationship::getUserId)
                .collect(Collectors.toList());
    }

    public void saveData(){data = data;}

    // 유저-관계 존재 여부 확인
    public boolean isExist(UUID userId, UUID channelId){
        return data.stream().anyMatch(uc ->
                uc.getUserId().equals(userId) && uc.getChannelId().equals(channelId));
    }
}
