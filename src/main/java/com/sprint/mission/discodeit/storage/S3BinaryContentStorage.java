package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(
        prefix = "discodeit.storage",
        name = "type",
        havingValue = "s3"
)
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final int presignedUrlExpiration;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.S3.access-key}") String accessKey,
            @Value("${discodeit.storage.S3.secret-key}") String secretKey,
            @Value("${discodeit.storage.S3.region}") String region,
            @Value("${discodeit.storage.S3.bucket}") String bucket,
            @Value("${discodeit.storage.S3.presigned-url-expiration}") int presignedUrlExpiration
    ) {

        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;

        System.out.println("S3BinaryContentStorage Bean initialized. Bucket: " + this.bucket);

    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        return null;
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        // S3 객체 키는 BinaryContentDto의 ID를 사용합니다.
        String key = metaData.id().toString();
        String contentType = metaData.contentType();

        // 1. Presigned URL 생성
        String presignedUrl = generatePresignedUrl(key, contentType);

        // 2. 302 Found (Redirect) 응답 반환
        return ResponseEntity
                .status(HttpStatus.FOUND) // 302 Found
                .location(URI.create(presignedUrl))
                // 다운로드 시 파일명을 브라우저에 명시적으로 지정
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metaData.fileName() + "\"")
                .build();
    }

    public Object getS3Client() {
        return null;
    }

    private String generatePresignedUrl(String key, String contentType) {
        long expiryTimeInSeconds = System.currentTimeMillis() / 1000L + presignedUrlExpiration;
        return String.format("https://%s.s3.%s.amazonaws.com/%s?Expires=%d&Signature=MOCK_SIGNATURE",
                bucket, region, key, expiryTimeInSeconds);
    }
}
