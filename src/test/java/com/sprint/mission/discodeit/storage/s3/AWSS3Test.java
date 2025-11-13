package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.profiles.active=prod",
    "spring.cloud.aws.credentials.access-key=",
    "spring.cloud.aws.credentials.secret-key=",
    "spring.cloud.aws.region.static=",
    "spring.cloud.aws.s3.bucket=",
    "discodeit.storage.s3.presigned-url-expiration=PT15M",
    "discodeit.storage.type=s3"
})
public class AWSS3Test {

  @Autowired
  private S3Client s3Client;
  @Autowired
  private S3Presigner s3Presigner;

  @Value("${spring.cloud.aws.credentials.access-key}")
  private String accessKey;
  @Value("${spring.cloud.aws.credentials.secret-key}")
  private String secretKey;
  @Value("${spring.cloud.aws.region.static}")
  private String region;
  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucket;
  @Value("${discodeit.storage.s3.presigned-url-expiration}")
  private String presignedUrlExpiration;

  private final UUID testUuid = UUID.randomUUID();
  private byte[] data = "This is a test data for S3 storage.".getBytes();

  @Test
  void testS3Upload() {

    //given
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(testUuid.toString()).build());
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(testUuid.toString())
        .build();

    //when & then
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(testUuid.toString()).build());

  }

  @Test
  void testS3Download() {

    //given
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(testUuid.toString())
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(testUuid.toString())
        .build();

    //when
    byte[] downloadedData = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

    //then
    assertEquals(new String(data), new String(downloadedData));
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(testUuid.toString()).build());

  }

  @Test
  void testS3PresignedUrl() {

    //given
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(testUuid.toString())
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(testUuid.toString())
        .build();

    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(java.time.Duration.parse(presignedUrlExpiration))
        .getObjectRequest(getObjectRequest)
        .build();

    //when
    String presignedUrl = s3Presigner.presignGetObject(getObjectPresignRequest)
        .url()
        .toString();

    //then
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(testUuid.toString()).build());

  }

}
