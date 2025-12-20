package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component("Message")
@RequiredArgsConstructor
public class MessagePermissionEvaluator implements DomainPermissionEvaluator {

    private final MessageRepository messageRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String permission) {
        Message message = messageRepository.findById((UUID) targetId)
                .orElseThrow(MessageNotFoundException::new);

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        if(isAdmin) return true;

        return message.getAuthor().getUsername().equals(authentication.getPrincipal());
    }
}
