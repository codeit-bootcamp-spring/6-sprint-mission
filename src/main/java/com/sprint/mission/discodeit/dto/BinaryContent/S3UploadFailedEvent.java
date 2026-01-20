package com.sprint.mission.discodeit.dto.BinaryContent;

import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.UUID;

public record S3UploadFailedEvent(
        UUID id,
        S3Exception e
        ) {
}
