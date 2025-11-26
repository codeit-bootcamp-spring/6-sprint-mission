package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("dev")
public class S3BinaryContentStorageTest {
    @Autowired
    private S3BinaryContentStorage s3BinaryContentStorage;

    @Test
    @DisplayName("S3 파일 업로드 및 다운로드 URL 생성")
    public void testDownload() {
        // given
        UUID fileId = UUID.randomUUID();
        String content = "S3 업로드 테스트";
        byte[] fileData = content.getBytes();
        BinaryContentDto binaryContentDto = new BinaryContentDto(
                fileId, "test.txt",
                (long) fileData.length, "test/plain");

        // when
        s3BinaryContentStorage.put(fileId, fileData); // 업로드
        ResponseEntity<Void> response = s3BinaryContentStorage.download(binaryContentDto);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(302);
        String presignedUrl = response.getHeaders().getLocation().toString();
        System.out.println("Generated Presigned URL: " +  presignedUrl);

        assertThat(presignedUrl).isNotNull();
        assertThat(presignedUrl).contains("https://");
        assertThat(presignedUrl).contains("X-Amz-Signature");
    }
}
