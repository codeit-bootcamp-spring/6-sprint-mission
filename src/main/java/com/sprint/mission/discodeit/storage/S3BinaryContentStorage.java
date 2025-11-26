package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;

    private final String secretKey;

    private final String region;

    private final String bucket;

    private final int presignedTime;


    S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration}") int presignedTime) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedTime = presignedTime;
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key("images/" + binaryContentId.toString())
                .build();

        getS3Client().putObject(putObjectRequest, RequestBody.fromBytes(bytes));

        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key("images/" + binaryContentId.toString())
                .build();
        return getS3Client().getObject(getObjectRequest);
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {

        String s3Url = generatePresignedUrl("images/" + metaData.id(), metaData.contentType());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(java.net.URI.create(s3Url))
                .build();

    }

    S3Client getS3Client() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    String generatePresignedUrl(String key, String contentType) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentType(contentType)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignedTime))
                .getObjectRequest(getObjectRequest)
                .build();

        try (S3Presigner resigned = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build()
        ) {
            PresignedGetObjectRequest presignedGetObjectRequest = resigned.presignGetObject(getObjectPresignRequest);
            return presignedGetObjectRequest.url().toString();
        }
    }
}
