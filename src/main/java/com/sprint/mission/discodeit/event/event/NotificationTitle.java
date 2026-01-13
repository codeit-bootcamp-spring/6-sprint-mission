package com.sprint.mission.discodeit.event.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationTitle {
    CREATED_MESSAGE("새로운 메시지가 있습니다."),
    UPDATED_ROLE("권한이 변경되었습니다."),
    FILE_UPLOAD_FAILED("S3 파일 업로드 실패");

    private final String title;
}
