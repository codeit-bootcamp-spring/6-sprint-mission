package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User>findByEmail(String email);
    Optional<User>findByUsername(String username);
    Optional<User>findAllByRole(Role role);

    @Query("SELECT DISTINCT u FROM User " +
            "u LEFT JOIN FETCH u.profile "
    )
    List<User> findAll();


    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}
