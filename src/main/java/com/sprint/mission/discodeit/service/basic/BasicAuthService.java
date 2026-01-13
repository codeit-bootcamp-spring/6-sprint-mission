package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.model.JwtInformation;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService detailsService;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher eventPublisher;

  public boolean isOwner(UUID id, Object principal) {
    if (!(principal instanceof DiscodeitUserDetails userDetails)) {
      return false;
    }
    return userDetails.getUserDto().id().equals(id);
  }

  public boolean isMessageAuthor(UUID messageId, Object principal) {
    if (!(principal instanceof DiscodeitUserDetails userDetails)) {
      return false;
    }
    Message message = messageRepository.findById(messageId)
        .orElseThrow(MessageNotFoundException::new);
    UUID authorId = message.getAuthor().getId();
    return userDetails.getUserDto().id().equals(authorId);
  }

  @Transactional
  public User updateRole(RoleUpdateRequest updateRequest) {
    User user = userRepository.findById(updateRequest.userId())
        .orElseThrow(UserNotFoundException::new);
    boolean isUpdated = user.updateRole(updateRequest.newRole());
    if (isUpdated) {
      eventPublisher.publishEvent(RoleUpdatedEvent.builder()
          .userId(user.getId())
          .oldRole(user.getRole())
          .newRole(updateRequest.newRole())
          .build());
    }

    return user;
  }

  @Transactional
  public JwtInformation refreshToken(String refreshToken) {
    // 1. 검증: 서명 유효성 확인 AND DB 존재 여부 확인
    if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken) ||
        !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      return null;
    }

    // 2. 정보 조회
    String username = jwtTokenProvider.getClaims(refreshToken).getSubject();
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) detailsService.loadUserByUsername(username);

    // 3. 새 토큰 생성
    String newAccess = jwtTokenProvider.createAccessToken(userDetails);
    String newRefresh = jwtTokenProvider.createRefreshToken(userDetails);

    // 4. 레지스트리 갱신
    JwtInformation newInfo = new JwtInformation(userDetails.getUserDto(), newAccess, newRefresh);
    jwtRegistry.rotateJwtInformation(refreshToken, newInfo);

    return newInfo;
  }
}
