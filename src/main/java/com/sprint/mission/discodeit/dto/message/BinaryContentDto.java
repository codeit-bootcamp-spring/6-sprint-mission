package com.sprint.mission.discodeit.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BinaryContentDto(
        UUID id,
        String contentType,
        String filename,
        Long size,
        @JsonIgnore byte[] data
) {
    public static BinaryContentDto from(MultipartFile file, UUID id) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Attachment file cannot be empty.");
        }

        return new BinaryContentDto(
                id,
                file.getContentType(),
                file.getOriginalFilename(),
                file.getSize(),
                file.getBytes()
        );
    }
}