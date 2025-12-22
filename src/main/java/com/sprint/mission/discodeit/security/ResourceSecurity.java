package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("resourceSecurity")
@RequiredArgsConstructor
public class ResourceSecurity {

    private final MessageRepository messageRepository;

    public boolean isUserOwner(UUID userId, DiscodeitUserDetails principal) {
        if (principal == null || principal.getUserDto() == null) {
            return false;
        }
        return userId != null && userId.equals(principal.getUserDto().id());
    }

    public boolean isMessageAuthor(UUID messageId, DiscodeitUserDetails principal) {
        if (principal == null || principal.getUserDto() == null) {
            return false;
        }
        UUID userId = principal.getUserDto().id();
        if (messageRepository.existsByIdAndAuthor_Id(messageId, userId)) {
            return true;
        }
        if (!messageRepository.existsById(messageId)) {
            throw MessageNotFoundException.withId(messageId);
        }
        return false;
    }
}
