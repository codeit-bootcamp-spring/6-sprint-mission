package com.sprint.mission.discodeit.security.evaluator;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("Channel")
@RequiredArgsConstructor
@Slf4j
public class ChannelPermissionEvaluator implements DomainPermissionEvaluator {
    private final ChannelRepository channelRepository;

    @Override
    public boolean hasPermission(Authentication authentication, UUID targetId, String permission) {
        return switch (permission) {
            case "deletePublic" -> isPublic(authentication, targetId);
            case "deletePrivate" -> !isPublic(authentication, targetId);
            default -> {
                log.warn("{} is not a valid permission", permission);
                yield false;
            }
        };
    }

    private boolean isPublic(Authentication authentication, UUID targetId) {
        Channel foundChannel = channelRepository.findById(targetId)
                .orElseThrow(() -> ChannelNotFoundException.withId(targetId));

        return foundChannel.getType().equals(ChannelType.PUBLIC);
    }
}
