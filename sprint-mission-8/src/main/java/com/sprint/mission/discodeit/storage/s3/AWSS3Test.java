package com.sprint.mission.discodeit.storage.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class AWSS3Test {

  private final S3Client s3Client;
  private final S3Presigner presigner;
  private final String bucketName;

  public AWSS3Test() throws IOException {
    // 1. .env 파일에서 설정 읽기
    Properties properties = new Properties();
    try (InputStream is = new FileInputStream(
        "/Users/hyunwook/Documents/Server/6-sprint-mission/sprint-mission-8/.env"
    )) {
      properties.load(is);
    }

    String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
    String regionStr = properties.getProperty("AWS_S3_REGION");
    this.bucketName = properties.getProperty("AWS_S3_BUCKET");

    if (accessKey == null || secretKey == null || regionStr == null || bucketName == null) {
      throw new IllegalStateException(".env 설정이 부족합니다. (AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY / AWS_REGION / AWS_S3_BUCKET)");
    }

    Region region = Region.of(regionStr);

    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

    // 2. S3Client, Presigner 생성
    this.s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    this.presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  /**
   * S3에 파일 업로드 테스트
   */
  public void uploadTest(Path localFilePath, String s3Key) throws IOException {
    System.out.println("=== S3 업로드 테스트 ===");
    System.out.println("Local File : " + localFilePath);
    System.out.println("Bucket     : " + bucketName);
    System.out.println("Key        : " + s3Key);

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .build();

    s3Client.putObject(
        request,
        RequestBody.fromBytes(Files.readAllBytes(localFilePath))
    );

    System.out.println("업로드 완료 ✅");
  }

  /**
   * S3에서 파일 다운로드 테스트
   */
  public void downloadTest(String s3Key, Path downloadTargetPath) throws IOException {
    System.out.println("=== S3 다운로드 테스트 ===");
    System.out.println("Bucket   : " + bucketName);
    System.out.println("Key      : " + s3Key);
    System.out.println("Save As  : " + downloadTargetPath);

    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .build();

    s3Client.getObject(request, downloadTargetPath);

    System.out.println("다운로드 완료 ✅");
  }

  /**
   * Presigned URL 생성 테스트 (GET 다운로드용)
   */
  public void presignedUrlTest(String s3Key, Duration expire) {
    System.out.println("=== Presigned URL 생성 테스트 ===");
    System.out.println("Bucket : " + bucketName);
    System.out.println("Key    : " + s3Key);
    System.out.println("Expire : " + expire);

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(expire)
        .getObjectRequest(getObjectRequest)
        .build();

    URL url = presigner.presignGetObject(presignRequest).url();

    System.out.println("Presigned URL ✅");
    System.out.println(url.toString());
  }
}