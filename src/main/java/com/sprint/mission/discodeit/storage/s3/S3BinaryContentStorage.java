package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;
    private final Duration expiration;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.bucket}")
            String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration}")
            int expiration,
            S3Client s3Client,
            S3Presigner s3Presigner
    ) {
        this.bucket = bucket;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.expiration = Duration.ofSeconds(expiration);
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(binaryContentId.toString())
                    .build();

            RequestBody requestBody = RequestBody.fromBytes(bytes);
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (S3Exception e) {
            throw new RuntimeException(e);
        }

        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(binaryContentId.toString())
                    .build();
            return s3Client.getObject(getObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(metaData.id().toString())
                    .build();
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(expiration)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(presignedGetObjectRequest.url().toString())).build();
        } catch (S3Exception e) {
            throw new RuntimeException(e);
        }
    }
}
