package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class S3BinaryContentStorageTest {

  private static BinaryContentStorage storage;

  /**
   * 프로젝트 루트에서 .env 를 자동으로 탐색하는 메소드
   */
  private static Path findProjectRoot() {
    Path current = Paths.get("").toAbsolutePath();

    while (current != null) {
      Path envPath = current.resolve(".env");
      if (Files.exists(envPath)) {
        return current;   // 여기에서 찾으면 성공
      }
      current = current.getParent();
    }

    throw new IllegalStateException(".env 파일을 찾을 수 없습니다.");
  }

  @BeforeAll
  static void setup() throws IOException {
    // 프로젝트 루트 찾기
    Path root = findProjectRoot();
    Path envPath = root.resolve(".env");

    Properties props = new Properties();

    try (InputStream in = Files.newInputStream(envPath)) {
      props.load(in);
    }

    String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    String region    = props.getProperty("AWS_S3_REGION");
    String bucket    = props.getProperty("AWS_S3_BUCKET");

    if (accessKey == null || secretKey == null || region == null || bucket == null) {
      throw new IllegalStateException(".env 설정이 부족합니다. (AWS_S3_ACCESS_KEY / SECRET / REGION / BUCKET)");
    }

    storage = new S3BinaryContentStorage(accessKey, secretKey, region, bucket, 600);
  }

  @Test
  void put_and_get() throws Exception {
    UUID id = UUID.randomUUID();
    byte[] data = "hello from S3BinaryContentStorageTest".getBytes();

    storage.put(id, data);

    try (InputStream in = storage.get(id)) {
      byte[] downloaded = in.readAllBytes();
      assertThat(new String(downloaded))
          .isEqualTo("hello from S3BinaryContentStorageTest");
    }
  }

  @Test
  void download_returns_redirect_with_presigned_url() throws Exception {
    UUID id = UUID.randomUUID();
    byte[] data = "for presigned url".getBytes(StandardCharsets.UTF_8);

    storage.put(id, data);

    // DTO 생성자: (UUID id, String fileName, Long size, String contentType)
    BinaryContentDto dto = new BinaryContentDto(
        id,
        "test.txt",
        (long) data.length,
        "text/plain"
    );

    ResponseEntity<?> response = storage.download(dto);

    assertThat(response.getStatusCode().is3xxRedirection()).isTrue();
    assertThat(response.getHeaders().getLocation()).isNotNull();

    System.out.println("리다이렉트 URL = " + response.getHeaders().getLocation());
  }
}