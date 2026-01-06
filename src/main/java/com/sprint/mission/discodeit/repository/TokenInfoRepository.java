package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenInfoRepository extends JpaRepository<TokenInfo, UUID> {

    Optional<TokenInfo> findByUserId(UUID userId);
    Optional<TokenInfo> findByAccessToken(String accessToken);
    Optional<TokenInfo> findByRefreshToken(String refreshToken);
    void deleteByUserId(UUID userId);
}
