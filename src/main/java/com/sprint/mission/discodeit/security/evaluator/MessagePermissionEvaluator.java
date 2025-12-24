package com.sprint.mission.discodeit.security.evaluator;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.security.userDetails.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("Message")
@RequiredArgsConstructor
public class MessagePermissionEvaluator implements DomainPermissionEvaluator {
    private final MessageRepository messageRepository;

    @Override
    public boolean hasPermission(Authentication authentication, UUID targetId, String permission) {
        return switch (permission) {
            case "update", "delete" -> isOwner(authentication, targetId);
            default -> {
                log.warn("{} is not a valid permission", permission);
                yield false;
            }
        };
    }

    private boolean isOwner(Authentication authentication, UUID targetId) {
        Message message = messageRepository.findById(targetId)
                .orElseThrow(() -> MessageNotFoundException.withId(targetId));

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            return message.getAuthor().getId().equals(userDetails.getUserDto().id());
        }

        return false;
    }
}
