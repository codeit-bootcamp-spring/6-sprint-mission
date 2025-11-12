package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage{

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  /*@Value("${cloud.aws.credentials.access-key}")
  private String accessKey;
  @Value("${cloud.aws.credentials.secret-key}")
  private String secretKey;
  @Value("${cloud.aws.region.static}")
  private String region;*/
  @Value("${cloud.aws.s3.bucket}")
  private String bucket;
  @Value("${discodeit.storage.s3.presigned-url-expiration}")
  private String presignedUrlExpiration;

  @Override
  public UUID put(UUID id, byte[] bytes) {

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

    log.info("Success to put object to S3 with id: {}", id);

    return id;

  }

  @Override
  public void deleteById(UUID id) {

    try {

      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucket)
          .key(id.toString())
          .build();

      s3Client.deleteObject(deleteObjectRequest);
      log.info("Success to delete object from S3 with id: {}", id);

    } catch (Exception e) {
      log.error("Failed to delete object from S3 with id: {}", id, e);
    }

  }

  @Override
  public InputStream get(UUID id) {

    try {

      ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
          builder -> builder.bucket(bucket).key(id.toString())
      );
      log.info("Success to get object from S3 with id: {}", id);

      return s3Object;

    } catch (Exception e) {

      log.error("Failed to get object from S3 with id: {}", id, e);
      throw new IllegalArgumentException("Failed to get object from S3 with id: " + id);

    }

  }

  @Override
  public ResponseEntity<?> download(BinaryContentDTO.BinaryContent binaryContent) {

    if (binaryContent.getId() == null) {
      throw new IllegalArgumentException("Invalid file id.");
    }

    String presignedUrl = generatePresignedUrl(
        binaryContent.getId().toString(),
        binaryContent.getContentType().name()
    );

    return ResponseEntity.status(302)
        .header("Location", presignedUrl)
        .build();

  }

  public String generatePresignedUrl(String key, String contentType) {

    try {

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .responseContentType(contentType)
          .build();

      GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(java.time.Duration.parse(presignedUrlExpiration))
          .getObjectRequest(getObjectRequest)
          .build();

      String presignedUrl = s3Presigner.presignGetObject(getObjectPresignRequest)
          .url().toString();

      log.info("Success to get object from S3 with id: {}", key);
      return presignedUrl;

    } catch (Exception e) {

      log.error("Failed to get object from S3 with id: {}", key, e);
      return  null;

    }

  }

}
