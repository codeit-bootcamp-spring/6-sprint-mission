package com.sprint.mission.discodeit.event.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationContentFormatter {
    CREATED_MESSAGE("%s: %s"),
    UPDATED_ROLE("%s -> %s"),
    FILE_UPLOAD_FAILED("""
            %s: %s
            %s: %s
            %s: %s
            """);

    private final String formatter;
}
