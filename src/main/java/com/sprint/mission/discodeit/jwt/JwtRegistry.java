package com.sprint.mission.discodeit.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;

public interface JwtRegistry {
    void registerJwtInformation(JwtInformation jwtInformation);

    void invalidateJwtInformationByUserId(String userId);

    void invalidateJwtInformationByRefreshToken(String refreshToken);

    boolean hasActiveJwtInformationByUserId(String userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    void clearExpiredJwtInformation();
}
