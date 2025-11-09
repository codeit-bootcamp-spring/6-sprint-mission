package com.sprint.mission.discodeit.dto.BinaryContent;

import lombok.Builder;

import java.util.UUID;
@Builder
public record BinaryContentDto (
        UUID id,
        String fileName,
        Long size,
        String contentType
){
}
