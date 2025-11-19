package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "discodeit.storage",
    name = "type",
    havingValue = "s3"
)
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Template s3Template;

  private final S3Client s3Client;

  @Value("${discodeit.storage.s3.bucket}")
  private String s3Bucket;

  @Value("${discodeit.storage.s3.presigned-url-expiration}")
  private int urlExpirationSeconds;

  @Override
  public UUID put(UUID binaryContentId, byte[] data) {
    s3Template.upload(s3Bucket, binaryContentId.toString(), new ByteArrayInputStream(data));
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) throws IOException {
    S3Resource s3Resource = s3Template.download(s3Bucket, binaryContentId.toString());
    return s3Resource.getInputStream();
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    try {
      byte[] bytes = get(binaryContentDto.id()).readAllBytes();

      log.info("파일 다운로드: {}, 이름: {}", binaryContentDto.id(), binaryContentDto.fileName());
      return ResponseEntity.ok(bytes);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 다운로드 실패");
    }
  }

  private S3Client getS3Client() {
    return s3Client;
  }

  private String generatePresignedUrl(String key, String contentType) {
    URL presigned = s3Template.createSignedGetURL(s3Bucket, key, Duration.of(urlExpirationSeconds, ChronoUnit.SECONDS));
    return presigned.toString();
  }
}
