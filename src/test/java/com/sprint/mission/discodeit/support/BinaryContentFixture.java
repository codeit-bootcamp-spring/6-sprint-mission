package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;

import java.lang.reflect.Field;
import java.util.UUID;

public class BinaryContentFixture {
    public static void setBinaryContentId(BinaryContent binaryContent, UUID binaryContentId) {
        try {
            Class<?> binaryContentClass = binaryContent.getClass().getSuperclass();
            Field idField = binaryContentClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(binaryContent, binaryContentId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
