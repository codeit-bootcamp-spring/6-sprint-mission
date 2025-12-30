package com.sprint.mission.discodeit.stoarge.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

class AWSS3Test {

    private static S3Client s3Client;
    private static S3Presigner s3Presigner;
    private static String bucketName;

    @BeforeAll
    static void setUpClient() throws IOException {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path envPath = projectRoot.resolve(".env");

        Properties env = new Properties();
        try (Reader reader = Files.newBufferedReader(envPath, StandardCharsets.UTF_8)) {
            env.load(reader);
        }

        String accessKey = require(env, "AWS_S3_ACCESS_KEY");
        String secretKey = require(env, "AWS_S3_SECRET_KEY");
        String region = require(env, "AWS_S3_REGION");
        bucketName = requireWithFallback(env, "AWS_S3_BUCKET", "AWS_S3_BUCKET_NAME");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        Region awsRegion = Region.of(region);

        s3Client = S3Client.builder()
            .region(awsRegion)
            .credentialsProvider(credentialsProvider)
            .build();

        s3Presigner = S3Presigner.builder()
            .region(awsRegion)
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @AfterAll
    static void tearDown() {
        if (s3Client != null) {
            s3Client.close();
        }
        if (s3Presigner != null) {
            s3Presigner.close();
        }
    }

    @Test
    void uploadObject() {
        String key = buildObjectKey("upload");
        String payload = "s3 upload test :: " + Instant.now();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("text/plain")
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(payload, StandardCharsets.UTF_8));

        HeadObjectResponse headObjectResponse = s3Client.headObject(HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build());

        assertEquals(payload.getBytes(StandardCharsets.UTF_8).length, headObjectResponse.contentLength());
        deleteObjectQuietly(key);
    }

    @Test
    void downloadObject() throws IOException {
        String key = buildObjectKey("download");
        String expected = "download verification payload :: " + UUID.randomUUID();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("text/plain")
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(expected, StandardCharsets.UTF_8));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
            String actual = new String(response.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(expected, actual);
        } finally {
            deleteObjectQuietly(key);
        }
    }

    @Test
    void generatePresignedUrl() {
        String key = buildObjectKey("presign");
        String payload = "presigned get :: " + Instant.now();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("text/plain")
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(payload, StandardCharsets.UTF_8));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .getObjectRequest(getObjectRequest)
            .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        assertThat(presignedRequest).isNotNull();
        assertThat(presignedRequest.url().toString()).contains(bucketName);

        deleteObjectQuietly(key);
    }

    private static String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing '" + key + "' in .env file");
        }
        return value.trim();
    }

    private static String requireWithFallback(Properties properties, String primary, String fallback) {
        String primaryValue = properties.getProperty(primary);
        if (primaryValue != null && !primaryValue.isBlank()) {
            return primaryValue.trim();
        }
        return require(properties, fallback);
    }

    private static String buildObjectKey(String prefix) {
        return "tests/" + prefix + "-" + UUID.randomUUID() + ".txt";
    }

    private static void deleteObjectQuietly(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
        } catch (Exception ignored) {
            // best-effort cleanup
        }
    }
}
