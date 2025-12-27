package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

//    Channel save(Channel channel);
//    Optional<Channel> findById(UUID id);
//    List<Channel> findAll();

    @Query("""
        SELECT DISTINCT c FROM Channel c
        LEFT JOIN FETCH c.readStatuses rs
        LEFT JOIN FETCH rs.user u
        LEFT JOIN FETCH u.profileImage
        WHERE c.id = :id
    """)
    Optional<Channel> findByIdWithUsers(@Param("id") UUID id);

    @Query("SELECT DISTINCT c FROM Channel c " +
            "LEFT JOIN FETCH c.messages m " +
            "LEFT JOIN FETCH m.author a " +
            "LEFT JOIN FETCH a.profileImage")
    List<Channel> findAllWithMessagesAndUsers();

}
