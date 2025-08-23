package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository {
    Channel createChannel(Channel channel);  // 생성
    Optional<Channel> readChannel(Long Id);      // 단건 읽기
    List<Channel> readAllChannels();   // 모두 읽기
    Optional<Channel> updateChannel(Channel channel);  // 수정
    boolean deleteChannel(Long Id);    // 삭제
}