package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.jwt.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserMapper userMapper;

    public JwtInformation refreshToken(String refreshToken) {
        // 1. 검증: 서명 유효성 확인 AND DB 존재 여부 확인
        if (refreshToken == null
                || !jwtTokenProvider.validateToken(refreshToken)
                || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)
        ) {
            throw new InvalidRefreshTokenException();
        }

        // 2. 정보 조회
        String username = jwtTokenProvider.getClaims(refreshToken).getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // 3. 새 토큰 생성 - 토큰 재발급 시에는 Access, Refresh 모두 새로 발급
        String newAccess = jwtTokenProvider.createAccessToken(username, user.getRole().name());
        String newRefresh = jwtTokenProvider.createRefreshToken(username, user.getRole().name());

        // 4. DB Rotation
        JwtInformation newInfo = new JwtInformation(
                userMapper.toDto(user),
                newAccess,
                newRefresh
        );
        jwtRegistry.rotateJwtInformation(refreshToken, newInfo);

        return newInfo;
    }
}
