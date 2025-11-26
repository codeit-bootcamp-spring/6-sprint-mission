package com.sprint.mission.discodeit.storage.s3;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.io.FileInputStream;

public class AWSS3Test
{
    private String bucket;
    private S3Client s3Client;
    private S3Presigner s3Presigner;

    @BeforeEach
    void setUp() throws IOException {
        // 환경 변수 로드
        Properties properties = new Properties();
        properties.load(new FileInputStream(".env"));

        String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        String regionString = properties.getProperty("AWS_S3_REGION");
        this.bucket = properties.getProperty("AWS_S3_BUCKET");

        // 공통 자격 증명 객체 생성
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        Region region = Region.of(regionString);

        // S3Client 생성 (업로드 / 다운로드)
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        this.s3Presigner =  S3Presigner.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Test
    @DisplayName("S3 파일 업로드")
    void uploadTest() {
        String key = "testFile.txt";
        String content = "Hello Hello S3";

        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(key).build(),
                RequestBody.fromString(content)
        );
        System.out.println("업로드 성공: " + key);
    }

    @Test
    @DisplayName("S3 파일 다운로드")
    void downloadTest() {
        String key = "testFile.txt";

        s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());
        System.out.println("다운로드 성공: " + key);
    }

    @Test
    @DisplayName("Presigned URL 생성")
    void presignedUrlTest() {
        String key = "testFile.txt";

        // 요청 정보
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // 서명 요청
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        // URL 발급
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        String url = presignedRequest.url().toString();

        System.out.println("Presigned URL: " + url);
    }
}