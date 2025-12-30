package com.sprint.mission.discodeit.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

class S3BinaryContentStorageTest {

    private static S3BinaryContentStorage storage;
    private static String bucketName;

    @BeforeAll
    static void setUp() throws IOException {
        Properties env = loadEnv();
        String accessKey = require(env, "AWS_S3_ACCESS_KEY");
        String secretKey = require(env, "AWS_S3_SECRET_KEY");
        String region = require(env, "AWS_S3_REGION");
        bucketName = requireWithFallback(env, "AWS_S3_BUCKET", "AWS_S3_BUCKET_NAME");
        long expiration =
            Long.parseLong(env.getProperty("AWS_S3_PRESIGNED_URL_EXPIRATION", "600").trim());

        storage = new S3BinaryContentStorage(accessKey, secretKey, region, bucketName, expiration);
    }

    @AfterAll
    static void tearDown() {
        if (storage != null) {
            storage.close();
        }
    }

    @Test
    void putShouldUploadPayload() {
        UUID binaryContentId = UUID.randomUUID();
        byte[] payload = ("upload-test::" + Instant.now()).getBytes(StandardCharsets.UTF_8);

        storage.put(binaryContentId, payload);

        HeadObjectResponse head = storage.getS3Client().headObject(HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(binaryContentId.toString())
            .build());

        assertEquals(payload.length, head.contentLength());
        deleteObjectQuietly(binaryContentId);
    }

    @Test
    void getShouldReturnStoredObject() throws IOException {
        UUID binaryContentId = UUID.randomUUID();
        byte[] payload = ("download-test::" + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8);
        storage.put(binaryContentId, payload);

        try (InputStream inputStream = storage.get(binaryContentId)) {
            byte[] actual = inputStream.readAllBytes();
            assertThat(actual).isEqualTo(payload);
        } finally {
            deleteObjectQuietly(binaryContentId);
        }
    }

    @Test
    void downloadShouldReturnRedirectResponse() {
        UUID binaryContentId = UUID.randomUUID();
        byte[] payload = ("redirect-test::" + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8);
        storage.put(binaryContentId, payload);
        BinaryContentDto metaData = new BinaryContentDto(
            binaryContentId,
            "redirect.txt",
            (long) payload.length,
            "text/plain"
        );

        ResponseEntity<?> response = storage.download(metaData);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).contains(bucketName);
        deleteObjectQuietly(binaryContentId);
    }

    @Test
    void generatePresignedUrlShouldIncludeBucketName() {
        UUID binaryContentId = UUID.randomUUID();
        byte[] payload = ("presigned-test::" + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8);
        storage.put(binaryContentId, payload);

        String presignedUrl = storage.generatePresignedUrl(
            binaryContentId.toString(),
            "application/octet-stream"
        );

        assertThat(presignedUrl).contains(bucketName);
        deleteObjectQuietly(binaryContentId);
    }

    private static Properties loadEnv() throws IOException {
        Path envPath = Path.of(".env").toAbsolutePath();
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(envPath, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private static String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing '" + key + "' in .env file");
        }
        return value.trim();
    }

    private static String requireWithFallback(Properties properties, String primary,
        String fallback) {
        String primaryValue = properties.getProperty(primary);
        if (primaryValue != null && !primaryValue.isBlank()) {
            return primaryValue.trim();
        }
        return require(properties, fallback);
    }

    private static void deleteObjectQuietly(UUID binaryContentId) {
        try {
            storage.getS3Client().deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(binaryContentId.toString())
                .build());
        } catch (Exception ignored) {
            // best effort cleanup
        }
    }
}
