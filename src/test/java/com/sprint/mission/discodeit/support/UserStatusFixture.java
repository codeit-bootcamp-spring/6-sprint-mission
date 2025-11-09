package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.lang.reflect.Field;
import java.util.UUID;

public class UserStatusFixture {
    public static void setUserStatusId(UserStatus userStatus, UUID userStatusId) {
        try {
            Class<?> userStatusClass = userStatus.getClass().getSuperclass().getSuperclass();
            Field idField = userStatusClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(userStatus, userStatusId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
