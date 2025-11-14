package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(properties = {
        "discodeit.storage.type=s3",
        "discodeit.storage.S3.access-key=test-access-key",
        "discodeit.storage.S3.secret-key=test-secret-key",
        "discodeit.storage.S3.region=test-region",
        "discodeit.storage.S3.bucket=test-bucket",
        "discodeit.storage.S3.presigned-url-expiration=600"
})
@ActiveProfiles("test")
class S3BinaryContentStorageTest {

    @Autowired
    private BinaryContentStorage s3BinaryContentStorage;

    @Test
    @DisplayName("1. discodeit.storage.type=s3 일 때 Bean이 정상적으로 등록되어야 한다")
    void should_Be_Registered_When_StorageType_Is_S3() {
        assertThat(s3BinaryContentStorage).isNotNull();
        assertThat(s3BinaryContentStorage).isInstanceOf(S3BinaryContentStorage.class);
    }

    @Test
    @DisplayName("2. put 메서드는 UUID를 반환해야 한다")
    void put_Should_Return_UUID() {
        UUID testId = UUID.randomUUID();
        byte[] testBytes = new byte[]{1, 2, 3};
        UUID resultId = s3BinaryContentStorage.put(testId, testBytes);
        assertThat(resultId).isEqualTo(testId);
    }

    @Test
    @DisplayName("3. get 메서드는 InputStream을 반환해야 한다 (현재 null)")
    void get_Should_Return_InputStream() {
        UUID testId = UUID.randomUUID();
        InputStream inputStream = s3BinaryContentStorage.get(testId);
        assertThat(inputStream).isNull();
    }

    @Test
    @DisplayName("4. download 메서드는 ResponseEntity를 반환해야 한다")
    void download_Should_Return_ResponseEntity() {
        String fileName = "dummy_file.txt";
        BinaryContentDto metaData = new BinaryContentDto(
                UUID.randomUUID(),
                "dummy_file.txt",
                1024L,
                "application/octet-stream"
        );

        ResponseEntity<?> response = s3BinaryContentStorage.download(metaData);

        // 1. 상태 코드가 302 Found (리다이렉트)인지 확인
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        // 2. Location 헤더 (Presigned URL)가 존재하는지 확인
        assertThat(response.getHeaders().getLocation()).isNotNull();

        // 3. Location URL이 S3 Presigned URL 포맷을 따르는지 확인 (더미 구현 기준)
        String location = response.getHeaders().getLocation().toString();
        assertThat(location).containsPattern("^https://.*\\.s3\\..*\\.amazonaws\\.com/.*");

        // 4. Content-Disposition 헤더가 attachment와 파일명으로 설정되었는지 확인
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"" + fileName + "\"");

        // 5. 바디는 비어있어야 함 (리다이렉트 응답이므로)
        assertThat(response.getBody()).isNull();
    }
}