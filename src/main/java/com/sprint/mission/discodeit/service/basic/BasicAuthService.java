package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.TokenDTO;
import com.sprint.mission.discodeit.entity.TokenEntity;
import com.sprint.mission.discodeit.exception.user.InvalidJwtTokenException;
import com.sprint.mission.discodeit.mapper.TokenMapper;
import com.sprint.mission.discodeit.repository.TokenRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final TokenMapper tokenMapper;

  @Transactional
  @Override
  public TokenDTO renewAccessToken(String refreshToken) {

    TokenEntity tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
        .orElseThrow(InvalidJwtTokenException::new);

    if (jwtTokenProvider.validateRefreshToken(refreshToken)) {

      String username = jwtTokenProvider.getUsername(refreshToken);
      String role = userRepository.findByUsername(username)
          .orElseThrow(InvalidJwtTokenException::new)
          .getRole().name();
      String newAccessToken = jwtTokenProvider.generateAccessToken(username, role);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(username, role);

      tokenEntity.updateTokens(newAccessToken, newRefreshToken);

      log.info("Access token renewed for user: {}", username);

      return tokenMapper.toToken(tokenEntity);

    } else {
      throw new InvalidJwtTokenException();
    }

  }

}
