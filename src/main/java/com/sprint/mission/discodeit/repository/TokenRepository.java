package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.TokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

  Optional<TokenEntity> findByRefreshToken(String token);

}
