package com.sprint.mission.discodeit.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SseMessage {
    private String id;
    private Object data;
}
