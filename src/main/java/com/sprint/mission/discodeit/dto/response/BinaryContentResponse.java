package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record BinaryContentResponse(
        UUID id,
        String fileName,
        Long size,
        String contentType
) {

    public static BinaryContentResponse from(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType()
        );
    }

    public static List<BinaryContentResponse> fromList(List<BinaryContent> binaryContents) {
        return binaryContents.stream()
                .map(BinaryContentResponse::from)
                .collect(Collectors.toList());
    }
}