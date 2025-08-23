package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel ;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void createChannel(Channel channel);  // 생성
    Channel readChannel(String channel);      // 단건 읽기
    List<Channel> readAllChannels();   // 모두 읽기
    void updateChannel(Channel channel);  // 수정
    void deleteChannel(String channel);    // 삭제
}
