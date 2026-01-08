package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotificationNotFoundException extends NotificationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotificationNotFoundException(UUID notificationId) {
        super(
                ErrorCode.NOTIFICATION_NOT_FOUND,
                "알림을 찾을 수 없습니다. notificationId=" + notificationId,
                Map.of("notificationId", notificationId)
        );
    }
}
