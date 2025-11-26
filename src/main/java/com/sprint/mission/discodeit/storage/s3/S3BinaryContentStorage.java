package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PreDestroy;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

/**
 * Binary content storage backed by AWS S3. Provides the same behaviour as the local storage but
 * streams data from S3 or hands back presigned download URLs when requested.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

  private final String bucket;
  private final Duration presignedUrlExpiration;
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration}") long presignedUrlExpirationSeconds
  ) {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        Objects.requireNonNull(accessKey, "AWS access key must not be null"),
        Objects.requireNonNull(secretKey, "AWS secret key must not be null")
    );
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
    Region awsRegion = Region.of(Objects.requireNonNull(region, "AWS region must not be null"));
    this.bucket = Objects.requireNonNull(bucket, "S3 bucket must not be null");
    this.presignedUrlExpiration = Duration.ofSeconds(presignedUrlExpirationSeconds);
    this.s3Client = S3Client.builder()
        .region(awsRegion)
        .credentialsProvider(credentialsProvider)
        .build();
    this.s3Presigner = S3Presigner.builder()
        .region(awsRegion)
        .credentialsProvider(credentialsProvider)
        .build();
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    Objects.requireNonNull(binaryContentId, "binaryContentId must not be null");
    Objects.requireNonNull(bytes, "bytes must not be null");

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .contentType(DEFAULT_CONTENT_TYPE)
        .contentLength((long) bytes.length)
        .build();
    try {
      s3Client.putObject(request, RequestBody.fromBytes(bytes));
      return binaryContentId;
    } catch (SdkException e) {
      throw new RuntimeException("Failed to upload object to S3: " + binaryContentId, e);
    }
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    Objects.requireNonNull(binaryContentId, "binaryContentId must not be null");

    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .build();
    try {
      ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
      return response;
    } catch (NoSuchKeyException e) {
      throw new NoSuchElementException(
          "File with key " + binaryContentId + " does not exist on S3");
    } catch (SdkException e) {
      throw new RuntimeException("Failed to download object from S3: " + binaryContentId, e);
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto metaData) {
    Objects.requireNonNull(metaData, "metaData must not be null");

    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(metaData.id().toString())
        .responseContentType(resolveContentType(metaData.contentType()))
        .responseContentDisposition(buildContentDisposition(metaData.fileName()))
        .build();

    String presignedUrl = presignGetObject(request);
    URI redirectUri = URI.create(presignedUrl);
    return ResponseEntity
        .status(HttpStatus.FOUND)
        .location(redirectUri)
        .build();
  }

  /**
   * Generates a presigned URL for a stored object using the configured expiration.
   */
  public String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(resolveContentType(contentType))
        .build();
    return presignGetObject(request);
  }

  private String presignGetObject(GetObjectRequest request) {
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(presignedUrlExpiration)
        .getObjectRequest(request)
        .build();
    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    return presignedRequest.url().toString();
  }

  private String resolveContentType(String contentType) {
    if (contentType == null || contentType.isBlank()) {
      return DEFAULT_CONTENT_TYPE;
    }
    return contentType;
  }

  private String buildContentDisposition(String fileName) {
    if (fileName == null || fileName.isBlank()) {
      return "attachment";
    }
    String sanitized = fileName.replace("\"", "");
    return "attachment; filename=\"" + sanitized + "\"";
  }

  @PreDestroy
  public void close() {
    if (s3Client != null) {
      s3Client.close();
    }
    if (s3Presigner != null) {
      s3Presigner.close();
    }
  }

  public S3Client getS3Client() {
    return s3Client;
  }
}
