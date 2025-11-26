package com.sprint.mission.discodeit.service.repository;

import java.util.List;
import java.util.UUID;

public interface UserChannelRepositoryInterface {
    // 새 유저-채널 관계 추가
    boolean put(UUID userId, UUID channelId);

    // 유저-채널 단일 관계 삭제
    boolean remove(UUID userId, UUID channelId);

    // 특정 유저 모든 관계 삭제
    void removeAllOfUser(UUID userId);

    // 특정 채널 모든 관계 삭제
    void removeAllOfChannel(UUID channelId);

    // 유저 Id로 속한 채널 list 조회
    List<UUID> findChannelListOfUserId(UUID userId);

    // 채널 Id로 속한 유저 list 조회
    List<UUID> findUserListOfChannelId(UUID channelId);

    // 데이터 덮어쓰기 (저장)
    void saveData();

    // 유저-관계 존재 여부 확인
    boolean isExist(UUID userId, UUID channelId);
}
