package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.event.BinaryContentPutFailEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "S3")
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final BinaryContentRepository binaryContentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${discodeit.storage.s3.bucket}")
    private String bucketName;

    @Value("${discodeit.storage.s3.presigned-url-expiration}")
    private long presignedUrlExpirationSeconds;

    // S3에 저장
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {

        binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));

        if (bytes.length == 0) throw new IllegalArgumentException("바이트 데이터가 없습니다.");

        String key = binaryContentId.toString(); // UUID를 S3 Key로 사용.

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/octet-stream") // 바이너리 스트림 형태임을 명시. 없어도 AWS가 유추하긴 함.
                .contentLength((long) bytes.length)
                .build();

        try {
            s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));
            return binaryContentId;

        } catch (S3Exception e) {
            throw new RuntimeException("S3 파일 업로드 실패. key=" + key, e);
        }
    }

    @Recover
    public void recoverFromPut(S3Exception e, UUID id) {
        String requestId = MDC.get("requestId");
        log.error("S3 저장 최종 재시도 실패. requestId: {}, contentId: {}", requestId, id);
        eventPublisher.publishEvent(new BinaryContentPutFailEvent(requestId, id, e.getMessage()));
        throw e;
    }

    // S3에 저장된 파일 읽기
    @Override
    public InputStream get(UUID binaryContentId) {
        String key = binaryContentId.toString();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            return s3Client.getObject(getObjectRequest); // 서비스 단에서 스트림을 명시적으로 닫아줘야 함. otherwise 메모리누수 발생.
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error("S3 파일 불러오기 실패. key=" + key, e);
            throw new RuntimeException("S3 파일 불러오기 실패. key=" + key, e);
        }
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentResponseDto dto) {
        String key = dto.id().toString();
        String contentType = dto.contentType();
        String fileName = dto.fileName();

        try {
            // 1. Presigned URL 생성 메서드 호출
            String presignedUrl = generatePresignedUrl(key, contentType, fileName);

            // 2. HTTP 302 Found 상태와 Location 헤더를 사용하여 리다이렉트
            return ResponseEntity.status(HttpStatus.FOUND) // 302 Found 또는 303 See Other
                    .header(HttpHeaders.LOCATION, presignedUrl)
                    .build();

        } catch (S3Exception e) {
            log.error("S3 파일 다운로드 실패. " + e.getMessage());
            // S3 예외 처리
            if (e.statusCode() == 404) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    public String generatePresignedUrl(String key, String contentType, String fileName) {

        // 1. GET 요청 객체 생성: 버킷 이름, 키, Content-Disposition 지정
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                // 브라우저가 파일을 다운로드하도록 강제하고 파일 이름 지정
                .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                .responseContentType(contentType)
                .build();

        // 2. Presign 요청 객체 생성: GET 요청과 유효 기간(Duration) 설정
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignedUrlExpirationSeconds)) // 5분 동안 유효한 URL 설정
                .getObjectRequest(getObjectRequest)
                .build();

        // 3. Presigned URL 생성 및 결과 얻기
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        // 4. URL 문자열 반환
        return presignedRequest.url().toString();
    }
}
