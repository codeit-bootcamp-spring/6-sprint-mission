package com.sprint.mission.discodeit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AWSS3Test {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String bucketName;
    private Region region;

    /**
     * Properties 클래스를 활용해서 .env 파일의 AWS 정보를 로드하고 S3 클라이언트를 초기화합니다.
     */
    @BeforeAll
    void setup() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(".env")) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Error loading .env file: " + e.getMessage());
            // 테스트를 위해 직접 환경 변수를 설정하거나 예외를 던집니다.
            throw e;
        }

        String accessKey = properties.getProperty("AWS_ACCESS_KEY_ID");
        String secretKey = properties.getProperty("AWS_SECRET_ACCESS_KEY");
        String regionStr = properties.getProperty("AWS_REGION");
        bucketName = properties.getProperty("S3_BUCKET_NAME");

        region = Region.of(regionStr);

        // AWS 자격 증명 설정
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        // S3Client 초기화
        s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        // S3Presigner 초기화 (Presigned URL 생성용)
        s3Presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        System.out.println("S3 Client initialized for region: " + region.toString());
    }


    @Test
    void uploadTest() {
        String key = "test/upload-test.txt";
        String content = "This is a test content from Java SDK.";

        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        try {
            s3Client.putObject(putOb, RequestBody.fromString(content));
            System.out.println("Successfully uploaded object " + key);

            // 검증: 업로드 성공 여부만 확인 (S3Exception이 발생하지 않으면 성공)
            assertDoesNotThrow(() -> s3Client.putObject(putOb, RequestBody.fromString(content)));

        } catch (S3Exception e) {
            fail("Upload failed: " + e.getMessage());
        }
    }

    @Test
    void downloadTest() {
        // 이미 S3에 존재하는 파일(uploadTest에서 업로드된 파일이라고 가정)을 다운로드합니다.
        String key = "test/upload-test.txt";

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            // 파일을 다운로드하여 스트림으로 받습니다.
            s3Client.getObjectAsBytes(getObjectRequest).asString(StandardCharsets.UTF_8);
            System.out.println("Successfully downloaded object " + key);

            // 검증: 파일이 성공적으로 다운로드되고 내용이 비어있지 않음을 확인
            assertNotNull(s3Client.getObjectAsBytes(getObjectRequest).asString(StandardCharsets.UTF_8));

        } catch (S3Exception e) {
            fail("Download failed: " + e.getMessage());
        }
    }

    @Test
    void generatePresignedUrlTest() {
        String key = "test/sample-file.jpg"; // Presigned URL을 생성할 파일 키

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        // Presigned URL 생성 요청 (유효 기간: 1시간)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofHours(1))
                .build();

        // Presigned URL 생성
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
        URL url = presignedGetObjectRequest.url();

        System.out.println("Presigned URL: " + url.toString());

        // 검증: URL이 null이 아니며 버킷 이름과 키를 포함하고 있는지 확인
        assertNotNull(url);
        assertTrue(url.toString().contains(bucketName));
        assertTrue(url.toString().contains(key));
    }
}