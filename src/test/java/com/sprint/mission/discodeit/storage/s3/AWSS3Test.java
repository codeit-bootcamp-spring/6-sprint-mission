package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/*
테스트 환경에서는 yaml에 .env 주입이 안되기에 직접 프로퍼티로 설정
프로퍼티를 직접 넣지않는다면 yaml에서 가져올수 없어 오류나고 과금때문에 태그로 분리
./gradlew test --include-tag S3Test
 */
@SpringBootTest
@Tag("S3Test")
@TestPropertySource(properties = {
    "spring.cloud.aws.credentials.access-key=inputYourAccessKey",
    "spring.cloud.aws.credentials.secret-key=inputYourSecretKey",
    "spring.cloud.aws.region.static=inputYourRegion"
})
class AWSS3Test {

  private static String accessKey;
  private static String secretKey;
  private static String region;
  private static String bucket;

  // 프로퍼티를 통해 자동으로 주입되는 S3Template 사용
  @Autowired
  private S3Template s3Template;

  @BeforeAll
  static void setup() {
    Properties props = new Properties();

    try (InputStream in = Files.newInputStream(Path.of(".env"))) {
      props.load(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    region = props.getProperty("AWS_S3_REGION");
    bucket = props.getProperty("AWS_S3_BUCKET");
  }

  @Test
  @DisplayName("AWS S3 환경 변수 로드 테스트")
  void awsS3EnvLoadTest() {
    assertThat(accessKey).isNotNull();
    assertThat(secretKey).isNotNull();
    assertThat(region).isNotNull();
    assertThat(bucket).isNotNull();
  }

  @Test
  @DisplayName("AWS S3 업로드 테스트")
  void awsS3UploadTest() throws IOException {
    S3Resource s3Resource = s3Template.upload(bucket, "hello.txt", Files.newInputStream(Path.of("./hello.txt")));
    byte[] data = s3Resource.getInputStream().readAllBytes();
    String content = new String(data);

    assertThat(content.trim()).isEqualTo("Hello, AWS S3!");
  }

  @Test
  @DisplayName("AWS S3 다운로드 테스트")
  void awsS3DownloadTest() throws IOException {
    S3Resource s3Resource = s3Template.download(bucket, "hello.txt");
    byte[] data = s3Resource.getInputStream().readAllBytes();
    String content = new String(data);

    assertThat(content.trim()).isEqualTo("Hello, AWS S3!");
  }

  @Test
  @DisplayName("AWS S3 presign URL 생성 테스트")
  void awsS3PresignUrlTest() {
    URL presignUrl = s3Template.createSignedGetURL(bucket, "hello.txt", Duration.of(60, ChronoUnit.SECONDS));
    System.out.println("Presign URL: " + presignUrl);
    assertThat(presignUrl).isNotNull();
  }
}
