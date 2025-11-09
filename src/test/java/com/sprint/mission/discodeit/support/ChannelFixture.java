package com.sprint.mission.discodeit.support;


import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelFixture {

    public static Channel publicCreateChannel(String channelName, String description) {
        return Channel.builder()
                .channelName(channelName)
                .description(description)
                .build();
    }
    public static Channel privateCreateChannel() {
        return new Channel();
    }

    public static ChannelDto createChannelDto(Channel channel, List<UserDto> participants) {
        return ChannelDto.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .participants(participants)
                .lastMessageAt(Instant.now())
                .build();
    }

    public static void setChannelId(Channel channel, UUID channelId) {
        try {
            Class<?> channelClass = channel.getClass().getSuperclass().getSuperclass();
            Field idField = channelClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(channel, channelId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
