package com.sprint.mission.discodeit.event.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileUploadFailedEvent {
    private final String requestId;
    private final String binaryContentId;
    private final String error;
}
