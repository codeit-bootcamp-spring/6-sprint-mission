package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // *저장로직* 채널 생성
    Channel createChannel(String name, String information);

    // *저장로직* 전체 채널 조회
    List<Channel> findAll();

    // *비즈니스로직* 특정 유저의 모든 채널 조회
    List<Channel> findAllChannelsByUserId(UUID userId);

    // *비즈니스로직* 채널 이름 수정
    boolean updateName(Channel channel, String updatedName);

    // *저장로직* 채널 정보 수정
    boolean updateInformation(Channel channel, String updateInformation);

    // *비즈니스로직* 채널 삭제
    boolean deleteChannel(Channel channel);

}
