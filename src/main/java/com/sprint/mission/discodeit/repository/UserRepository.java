package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User " +
            "u LEFT JOIN FETCH u.profile " +
            "LEFT JOIN FETCH u.status"
    )
    List<User> findAll();

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}
