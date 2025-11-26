package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Presigner presigner;

  @Value("${discodeit.storage.s3.bucket}")
  private String bucket;

  @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
  private long presignedExpirationSeconds;

  // S3Client, S3Presigner 를 직접 생성하는 생성자
  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration:600}") long presignedExpirationSeconds
  ) {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

    Region awsRegion = Region.of(region);

    this.s3Client = S3Client.builder()
        .region(awsRegion)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    this.presigner = S3Presigner.builder()
        .region(awsRegion)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    this.bucket = bucket;
    this.presignedExpirationSeconds = presignedExpirationSeconds;
  }

  private String toKey(UUID id) {
    // S3 안의 파일 이름 규칙 (간단하게 UUID 문자열 그대로 사용)
    return id.toString();
  }

  @Override
  public UUID put(UUID id, byte[] content) {
    String key = toKey(id);

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.putObject(request, RequestBody.fromBytes(content));
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    String key = toKey(id);

    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    // ResponseInputStream 은 InputStream 이라 바로 리턴 가능
    return s3Client.getObject(request);
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) {
    UUID id = dto.id();                 // 실제 필드 이름은 프로젝트에 맞게 수정
    String contentType = dto.contentType(); // 없으면 null 허용

    String key = toKey(id);
    String url = generatePresignedUrl(key, contentType);

    // 302 Redirect 로 Presigned URL 로 보내기
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(url))
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(getObjectRequest)
        .signatureDuration(Duration.ofSeconds(presignedExpirationSeconds))
        .build();

    return presigner.presignGetObject(presignRequest)
        .url()
        .toString();
  }
}