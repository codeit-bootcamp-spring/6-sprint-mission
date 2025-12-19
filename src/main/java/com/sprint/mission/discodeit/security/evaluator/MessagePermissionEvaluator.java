package com.sprint.mission.discodeit.security.evaluator;

import com.sprint.mission.discodeit.dto.MessageDTO.Message;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.exception.message.NoSuchMessageException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.Serializable;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("Message")
@RequiredArgsConstructor
public class MessagePermissionEvaluator implements DomainPermissionEvaluator{

  private final MessageRepository messageRepository;

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String permission) {

    MessageEntity message = messageRepository.findById((UUID) targetId)
        .orElseThrow(NoSuchMessageException::new);

    boolean isAdmin = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"));
    if (isAdmin) return true;

    return message.getAuthor().getUsername().equals(authentication.getName());

  }
}
