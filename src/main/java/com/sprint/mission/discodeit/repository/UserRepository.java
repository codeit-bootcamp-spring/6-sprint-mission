package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  boolean existsById(UUID id);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmailOrUsername(String email, String username);

  @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileId WHERE u.id = :id")
  Optional<UserEntity> findById(@Param("id") UUID id);

  @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileId WHERE u.email = :email")
  Optional<UserEntity> findByEmail(@Param("email") String email);

  @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileId WHERE u.username = :username")
  Optional<UserEntity> findByUsername(@Param("username") String username);

  void deleteById(UUID id);

}
