package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.TokenInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenInfoRepository extends JpaRepository<TokenInfo, Long> {

    Optional<TokenInfo> findByUsername(String username);

    void deleteByUsername(String username);

    Optional<TokenInfo> findByRefreshToken(String refreshToken);

    Optional<TokenInfo> findByAccessToken(String accessToken);

}
