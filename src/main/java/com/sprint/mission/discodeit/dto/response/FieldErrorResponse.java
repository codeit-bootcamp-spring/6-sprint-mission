package com.sprint.mission.discodeit.dto.response;

public record FieldErrorResponse(
    String field,
    Object rejectedValue,
    String message
) {

}
