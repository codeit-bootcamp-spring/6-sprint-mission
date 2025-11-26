package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.enums.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UserResponseDto> participants, // PRIVATE 채널만 사용
        Instant lastMessageSentAt // null 허용
){
    // PUBLIC 채널용
    public static ChannelResponseDto publicChannel(UUID id, String name, String description, Instant lastMessageSentAt) {
        return new ChannelResponseDto(
                id,
                ChannelType.PUBLIC,
                name,
                description,
                List.of(),
                lastMessageSentAt
        );
    }

    // PRIVATE 채널용
    public static ChannelResponseDto privateChannel(UUID id, List<UserResponseDto> participants, Instant lastMessageSentAt) {
        return new ChannelResponseDto(
                id,
                ChannelType.PRIVATE,
                null,
                null,
                participants,
                lastMessageSentAt
        );
    }
}