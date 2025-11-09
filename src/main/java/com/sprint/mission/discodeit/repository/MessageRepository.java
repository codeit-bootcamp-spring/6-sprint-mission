package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {


    Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);
    List<Message> findByChannelIdInOrderByCreatedAtDesc(List<UUID> channelId);

    @Query("SELECT DISTINCT m FROM Message " +
            "m LEFT JOIN FETCH m.author " +
            "LEFT JOIN FETCH m.attachments "+
            "WHERE m.channel.id =:channelId")
    Slice<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    @Query("SELECT DISTINCT m FROM Message " +
            "m LEFT JOIN FETCH m.author " +
            "LEFT JOIN FETCH m.attachments " +
            "WHERE m.channel.id = :courseId AND m.createdAt > :lastCommentTime")
    Slice<Message> findByCourseIdAndIdLessThanOrderByIdDesc(
            @Param("courseId") UUID courseId,
            @Param("lastCommentTime") Instant lastCommentTime,
            Pageable pageable // size 정보만 사용
    );

}
