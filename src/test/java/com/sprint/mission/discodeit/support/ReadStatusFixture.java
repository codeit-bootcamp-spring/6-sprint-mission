package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.lang.reflect.Field;
import java.util.UUID;

public class ReadStatusFixture {

    public static void setReadStatusId(ReadStatus readStatus, UUID readStatusId) {
        try {
            Class<?> readStatusClass = readStatus.getClass().getSuperclass().getSuperclass();
            Field idField = readStatusClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(readStatus, readStatusId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
