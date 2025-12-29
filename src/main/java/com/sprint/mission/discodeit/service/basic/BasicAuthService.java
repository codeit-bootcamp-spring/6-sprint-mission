package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final SessionManager sessionManager;

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
    user.updateRole(updateRequest.newRole());

    expireUserSessions(updateRequest);

    return user;
  }

  private void expireUserSessions(RoleUpdateRequest updateRequest) {

    sessionManager.invalidateSessionsByUserId(updateRequest.userId());
  }
}
