package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatusEntity, UUID> {

  boolean existsById(UUID id);

  boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

  @Query("SELECT rs FROM ReadStatusEntity rs LEFT JOIN FETCH rs.user u LEFT JOIN FETCH rs.channel c WHERE rs.id = :id")
  Optional<ReadStatusEntity> findById(@Param("id") UUID id);

  @Query("SELECT rs FROM ReadStatusEntity rs LEFT JOIN FETCH rs.user u LEFT JOIN FETCH rs.channel c WHERE u.id = :userId AND c.id = :channelId")
  Optional<ReadStatusEntity> findByUserIdAndChannelId(@Param("userId") UUID userId,
      @Param("channelId") UUID channelId);

  @Query("SELECT rs FROM ReadStatusEntity rs LEFT JOIN FETCH rs.user u LEFT JOIN FETCH rs.channel c WHERE u.id = :userId")
  List<ReadStatusEntity> findByUserId(@Param("userId") UUID userId);

  @Query("SELECT rs FROM ReadStatusEntity rs LEFT JOIN FETCH rs.user u LEFT JOIN FETCH rs.channel c WHERE c.id = :channelId")
  List<ReadStatusEntity> findByChannelId(@Param("channelId") UUID channelId);

  void deleteById(UUID id);

  void deleteByUserIdAndChannelId(UUID userId, UUID channelId);

  void deleteByUserId(UUID userId);

  void deleteByChannelId(UUID channelId);

  void deleteAllByIdIn(Collection<UUID> idList);

}
