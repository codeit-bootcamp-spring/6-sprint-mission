package com.sprint.mission.discodeit.service.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChannelRepositoryInterface {
    // 채널 생성
    Channel createChannel(String name, String information);

    // 채널 아이디로 특정 채널 조회
    Channel findById(UUID channelId);

    // 전체 채널 조회
    List<Channel> findAll();

    void updateName(Channel channel, String updatedName);

    // 채널 정보 수정
    void updateInformation(Channel channel, String updateInformation);

    // 채널 삭제
    void deleteChannel(Channel channel);

    // 데이터 덮어쓰기 (저장)
    void saveData();

}
