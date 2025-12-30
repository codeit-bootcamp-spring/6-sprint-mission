package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.entity.TokenInfo;
import com.sprint.mission.discodeit.repository.TokenInfoRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaJwtRegistry implements JwtRegistry {

    private final TokenInfoRepository tokenInfoRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        String username = jwtInformation.getUserDto().username();
        TokenInfo tokenInfo = tokenInfoRepository.findByUsername(username)
            .map(existing -> TokenInfo.builder()
                .id(existing.getId())
                .username(username)
                .accessToken(jwtInformation.getAccessToken())
                .refreshToken(jwtInformation.getRefreshToken())
                .build())
            .orElseGet(() -> TokenInfo.builder()
                .username(username)
                .accessToken(jwtInformation.getAccessToken())
                .refreshToken(jwtInformation.getRefreshToken())
                .build());
        tokenInfoRepository.save(tokenInfo);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        userRepository.findById(userId)
            .flatMap(user -> tokenInfoRepository.findByUsername(user.getUsername()))
            .ifPresent(tokenInfoRepository::delete);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return tokenInfoRepository.findByRefreshToken(refreshToken).isPresent();
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        tokenInfoRepository.findByRefreshToken(refreshToken)
            .ifPresentOrElse(existing -> {
                TokenInfo updated = TokenInfo.builder()
                    .id(existing.getId())
                    .username(newJwtInformation.getUserDto().username())
                    .accessToken(newJwtInformation.getAccessToken())
                    .refreshToken(newJwtInformation.getRefreshToken())
                    .build();
                tokenInfoRepository.save(updated);
            }, () -> registerJwtInformation(newJwtInformation));
    }

    @Override
    public void clearExpiredJwtInformation() {
        tokenInfoRepository.findAll().stream()
            .filter(tokenInfo -> !jwtTokenProvider.validateToken(tokenInfo.getRefreshToken()))
            .forEach(tokenInfoRepository::delete);
    }
}
