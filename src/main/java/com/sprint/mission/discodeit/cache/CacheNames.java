package com.sprint.mission.discodeit.cache;

import java.util.List;

public final class CacheNames {
    // Channel
    public static final String USER_CHANNELS = "userChannels";

    // User
    public static final String USER_LIST = "userList";
    public static final String USER = "user";

    // Notification
    public static final String USER_NOTIFICATIONS = "userNotifications";

    private CacheNames() {
    }

    public static List<String> toList() {
        return List.of(
                USER_CHANNELS,
                USER_LIST,
                USER,
                USER_NOTIFICATIONS
        );
    }
}
