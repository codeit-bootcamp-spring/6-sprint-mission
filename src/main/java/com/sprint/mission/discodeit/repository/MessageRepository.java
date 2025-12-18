package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.MessageEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

  boolean existsById(UUID id);

  @Query("SELECT m FROM MessageEntity m LEFT JOIN FETCH m.author LEFT JOIN FETCH m.channel LEFT JOIN FETCH m.author.userStatus WHERE m.id = :id")
  Optional<MessageEntity> findById(@Param("id") UUID id);

  @Query("SELECT m FROM MessageEntity m LEFT JOIN FETCH m.author LEFT JOIN FETCH m.channel LEFT JOIN FETCH m.author.userStatus WHERE m.author.id = :authorId ORDER BY m.createdAt")
  Page<MessageEntity> findByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);

  @Query("SELECT m FROM MessageEntity m LEFT JOIN FETCH m.author LEFT JOIN FETCH m.channel LEFT JOIN FETCH m.author.userStatus WHERE m.channel.id = :channelId ORDER BY m.createdAt")
  Page<MessageEntity> findByChannelId(@Param("channelId") UUID channelId, Pageable pageable);

  @Query("SELECT m FROM MessageEntity m LEFT JOIN FETCH m.author LEFT JOIN FETCH m.channel LEFT JOIN FETCH m.author.userStatus WHERE m.channel.id = :channelId AND m.createdAt > :createdAt ORDER BY m.createdAt DESC LIMIT :size")
  Slice<MessageEntity> findByChannelIdAndCreatedAt(@Param("channelId") UUID channelId,
      @Param("createdAt") Instant createdAt, @Param("size") int size);

  @Query(value = "SELECT m FROM MessageEntity m LEFT JOIN FETCH m.author LEFT JOIN FETCH m.channel ORDER BY m.createdAt")
  Page<MessageEntity> findAll(Pageable pageable);

  void deleteById(UUID id);

  void deleteByChannelId(UUID channelId);

}
