package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;


import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MessageFixture {

    public static Message createMessage(User user, Channel channel, String content, List<BinaryContent> attachments) {
        return Message.builder()
                .author(user)
                .channel(channel)
                .content(content)
                .attachment(attachments)
                .build();
    }

    public static MessageDto createMessageDto(Message message , UserDto user, Channel channel, List<BinaryContentDto> attachmentsDto) {
        return MessageDto.builder()
                .id(message.getId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getCreatedAt())
                .content(message.getContent())
                .channelId(channel.getId())
                .author(user)
                .attachments(attachmentsDto)
                .build();
    }

    public static void setMessageId(Message message, UUID messageId) {
        try {
            Class<?> messageClass = message.getClass().getSuperclass().getSuperclass();
            Field idField = messageClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(message, messageId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setMessageCreatedAt(Message message, Instant createdAt) {
        try {
            Class<?> messageClass = message.getClass().getSuperclass().getSuperclass();
            Field createdAtField = messageClass.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(message, createdAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
