package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.entity.TokenInfo;
import com.sprint.mission.discodeit.repository.TokenInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class PersistentJwtRegistry implements JwtRegistry {

    private final TokenInfoRepository tokenInfoRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        tokenInfoRepository.deleteByUserId(userId);
        tokenInfoRepository.flush();

        TokenInfo tokenInfo = TokenInfo.builder()
                .userId(userId)
                .accessToken(jwtInformation.getAccessToken())
                .refreshToken(jwtInformation.getRefreshToken())
                .build();

        tokenInfoRepository.save(tokenInfo);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        tokenInfoRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return tokenInfoRepository.findByUserId(userId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return tokenInfoRepository.findByAccessToken(accessToken).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return tokenInfoRepository.findByRefreshToken(refreshToken).isPresent();
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        tokenInfoRepository.findByRefreshToken(refreshToken)
                .ifPresent(tokenInfo -> {
                   tokenInfo.rotate(
                           newJwtInformation.getAccessToken(),
                           newJwtInformation.getRefreshToken()
                   );
                });
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredJwtInformation() {
        tokenInfoRepository.findAll().forEach(tokenInfo -> {
            if(!jwtTokenProvider.validateToken(tokenInfo.getAccessToken())) {
                tokenInfoRepository.delete(tokenInfo);
            }
        });
    }
}
