package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.TokenEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

  Optional<TokenEntity> findByRefreshToken(String token);

  void deleteByRefreshToken(String token);

  void deleteByUserId(UUID userId);

}
