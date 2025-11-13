package com.sprint.mission.discodeit.storage.s3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AWSS3Test {

    private static S3Client s3;
    private static String bucketName;
    private static Properties awsProperties;
    private static final Path TEMP_DIR = Path.of("temp_s3_test");

    @BeforeAll
    static void setup() throws IOException {
        awsProperties = EnvironmentLoader.loadAwsProperties();

        String accessKey = awsProperties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = awsProperties.getProperty("AWS_S3_SECRET_KEY");
        String region = awsProperties.getProperty("AWS_S3_REGION");
        bucketName = awsProperties.getProperty("AWS_S3_BUCKET");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        Files.createDirectories(TEMP_DIR);
    }

    @Test
    void testUploadFile() throws IOException {
        String keyName = "test-upload-" + System.currentTimeMillis() + ".txt";
        Path tempFilePath = TEMP_DIR.resolve(keyName);
        Files.writeString(tempFilePath, "This is a test content for S3 upload.");

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType("text/plain")
                .build();

        s3.putObject(putRequest, RequestBody.fromFile(tempFilePath));

        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        assertNotNull(s3.headObject(headRequest));
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    void testDownloadFile() throws IOException {
        String keyName = "test-upload-123456789.txt";
        Path downloadPath = TEMP_DIR.resolve("downloaded-" + keyName);

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3.getObject(getRequest, ResponseTransformer.toFile(downloadPath));

        assertTrue(Files.exists(downloadPath));
        assertTrue(Files.size(downloadPath) > 0);
        Files.deleteIfExists(downloadPath);
    }

    @Test
    void testGeneratePresignedUrl() {
        String keyName = "test-upload-123456789.txt";

        S3Presigner presigner = S3Presigner.builder()
                .region(s3.serviceClientConfiguration().region())
                .credentialsProvider(s3.serviceClientConfiguration().credentialsProvider())
                .build();

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getRequest)
                .build();

        String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();

        assertTrue(presignedUrl.contains(bucketName));
        assertTrue(presignedUrl.contains("X-Amz-Signature"));
        presigner.close();
    }
}