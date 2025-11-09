package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.lang.reflect.Field;
import java.util.UUID;

public class UserFixture {

    public static UserDto createUserDto(
            UUID userId,
            String userName,
            String email,
            BinaryContentDto binaryContentDto,
            boolean online
    ) {
        return UserDto.builder()
                .id(userId)
                .username(userName)
                .email(email)
                .profile(binaryContentDto)
                .online(online)
                .build();

    }

    public static User createUser(BinaryContent binaryContent) {
        return User.builder()
                .username("테스트")
                .email("test@test.com")
                .password("000000")
                .profile(binaryContent)
                .status(null)
                .build();
    }


    public static void setStatus(User user, UserStatus status) {
        try {
            Class<?> userClass = User.class;
            Field field = userClass.getDeclaredField("status");
            field.setAccessible(true);
            field.set(user, status);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setUserId(User user, UUID userId) {
        try {
            Class<?> userClass = user.getClass().getSuperclass().getSuperclass();
            Field idField = userClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
