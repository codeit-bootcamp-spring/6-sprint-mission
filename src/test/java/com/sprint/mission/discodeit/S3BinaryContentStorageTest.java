package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

    @InjectMocks
    private S3BinaryContentStorage s3Storage;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private final String TEST_BUCKET = "test-bucket";
    private final String TEST_REGION = "ap-northeast-2";
    private final String TEST_ACCESS_KEY = "access";
    private final String TEST_SECRET_KEY = "secret";

    @BeforeEach
    void setUp() throws Exception {
        // 현재 S3BinaryContentStorage에 Setter가 없으므로 Reflection을 사용하거나,
        // 테스트만을 위한 생성자를 추가해야 합니다.
        // 여기서는 간단하게 Reflection을 통해 private 필드를 설정합니다.
        java.lang.reflect.Field bucketField = S3BinaryContentStorage.class.getDeclaredField("bucketName");
        bucketField.setAccessible(true);
        bucketField.set(s3Storage, TEST_BUCKET);
    }

    @Test
    @DisplayName("S3 PUT 성공: putObject가 호출되고 UUID가 반환되어야 함")
    void put_success() {
        // Given
        UUID binaryContentId = UUID.randomUUID();
        byte[] data = "test data".getBytes();

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // When
        UUID resultId = s3Storage.put(binaryContentId, data);

        // Then
        assertEquals(binaryContentId, resultId);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("S3 GET 성공: s3Client.getObject가 호출되고 InputStream이 반환되어야 함")
    void get_success() {
        // Given
        UUID fileId = UUID.randomUUID();
        byte[] streamData = "stream data".getBytes();
        InputStream mockInputStream = new ByteArrayInputStream(streamData);

        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> mockResponseInputStream =
                (ResponseInputStream<GetObjectResponse>) mock(ResponseInputStream.class);

        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(mockResponseInputStream); // Mock ResponseInputStream 반환

        // When
        InputStream resultStream = s3Storage.get(fileId);

        // Then
        assertNotNull(resultStream);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }


    @Test
    @DisplayName("Presigned URL 생성 성공: GetObjectPresignRequest에 Content-Disposition이 포함되어야 함")
    void generatePresignedUrl_success() throws MalformedURLException {
        // Given
        String key = "test-key";
        String contentType = "image/png";
        String fileName = "image.png";
        String expectedUrl = "https://mock-s3-url/image.png?signature=mock";

        // Mock URL 객체
        URL mockUrl = new URL(expectedUrl);

        // Mock PresignedGetObjectRequest 객체
        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(mockUrl);

        // Mock s3Presigner.presignGetObject 호출
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(mockPresignedRequest);

        // When
        String resultUrl = s3Storage.generatePresignedUrl(key, contentType, fileName);

        // Then
        assertEquals(expectedUrl, resultUrl);

        // GetObjectRequest에 responseContentDisposition이 올바르게 설정되었는지 확인
        ArgumentCaptor<GetObjectPresignRequest> presignRequestCaptor = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(s3Presigner).presignGetObject(presignRequestCaptor.capture());

        GetObjectRequest capturedGetObjectRequest = presignRequestCaptor.getValue().getObjectRequest();

        // responseContentDisposition 확인: 파일 다운로드와 파일 이름이 설정되었는지 검증
        assertEquals("attachment; filename=\"image.png\"", capturedGetObjectRequest.responseContentDisposition());
        // responseContentType 확인
        assertEquals(contentType, capturedGetObjectRequest.responseContentType());
    }


    @Test
    @DisplayName("DOWNLOAD 성공: Presigned URL로 HTTP 302 리다이렉션 응답 반환")
    void download_success_redirect() throws MalformedURLException {
        // Given
        UUID fileId = UUID.randomUUID();
        String expectedUrl = "https://redirect-s3-url/file?sig=abc";

        BinaryContentResponseDto dto = new BinaryContentResponseDto(
                fileId,
                "document.pdf",
                "application/pdf",
                1024L
        );

        // Mock generatePresignedUrl의 내부 호출 결과 (실제 generatePresignedUrl 호출)
        URL mockUrl = new URL(expectedUrl);
        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(mockUrl);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(mockPresignedRequest);

        // When
        ResponseEntity<Void> response = s3Storage.download(dto);

        // Then
        assertEquals(HttpStatus.FOUND, response.getStatusCode()); // 302 리다이렉션 상태 확인
        assertTrue(response.getHeaders().containsKey(HttpHeaders.LOCATION)); // Location 헤더 존재 확인
        assertEquals(expectedUrl, response.getHeaders().getFirst(HttpHeaders.LOCATION)); // URL 일치 확인

        // generatePresignedUrl이 올바른 인자로 호출되었는지 확인
        verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    @DisplayName("DOWNLOAD 실패: S3 404 Not Found 시 ResponseEntity.notFound() 반환")
    void download_fail_404_not_found() {
        // Given
        UUID fileId = UUID.randomUUID();
        BinaryContentResponseDto dto = new BinaryContentResponseDto(
                fileId, "nonexistent.txt", "text/plain", 0L
        );

        // Mock: Presign 과정에서 S3 404 예외 발생 가정
        AwsServiceException mockException = S3Exception.builder().statusCode(404).build();
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenThrow(mockException);

        // When
        ResponseEntity<Void> response = s3Storage.download(dto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // 404 상태 확인
        assertNull(response.getBody()); // 본문이 비어 있는지 확인
    }

    @Test
    @DisplayName("DOWNLOAD 실패: S3 기타 내부 오류 시 ResponseEntity.internalServerError() 반환")
    void download_fail_internal_error() {
        // Given
        UUID fileId = UUID.randomUUID();
        BinaryContentResponseDto dto = BinaryContentResponseDto.builder()
                .id(fileId)
                .fileName("error.dat")
                .contentType("application/octet-stream")
                .size(0L)
                .build();

        // Mock: Presign 과정에서 S3 500 계열 예외 발생 가정 (예: 500 Internal Error)
        AwsServiceException mockException = S3Exception.builder().statusCode(500).build();
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenThrow(mockException);

        // When
        ResponseEntity<Void> response = s3Storage.download(dto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // 500 상태 확인
    }
}
