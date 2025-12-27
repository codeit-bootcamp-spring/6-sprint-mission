package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Locked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    // 유저가 속한 채널 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 채널에 속한 유저 조회
    List<ReadStatus> findAllByChannelId(UUID channelId);

    @Query("""
    SELECT DISTINCT rs FROM ReadStatus rs
    LEFT JOIN FETCH rs.user u
    LEFT JOIN FETCH u.profileImage
    WHERE rs.channel.id = :channelId
    """)
    List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);

    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

}
