package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.userStatus
            LEFT JOIN FETCH u.profileImage
            WHERE u.username = :username
    """)
    Optional<User> findByUsernameWithStatusAndProfile(@Param("username") String username);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);

    Optional<User> findByEmail(String email);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.userStatus
        LEFT JOIN FETCH u.profileImage
    """)
    List<User> findAllWithStatusAndProfile();

}
