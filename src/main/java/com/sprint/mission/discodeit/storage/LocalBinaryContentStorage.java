package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentAlreadyExistsException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage{

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path root){
        this.root = root;
        log.info("root=" + root.toString());
    }

    // 루트 디렉토리 초기화
    @PostConstruct
    public void init() {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException("초기화에 실패했습니다.", e);
            }
        }
    }

    // 로컬에 저장
    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path filePath = resolvePath(id);
        if (Files.exists(filePath)) {
            throw new BinaryContentAlreadyExistsException(id);
        }
        try (OutputStream outputStream = Files.newOutputStream(filePath)){
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("업로드에 실패했습니다.", e);
        }
        return id;
    }

    // 저장된 파일 읽기
    @Override
    public InputStream get(UUID id) {
        Path filePath = resolvePath(id);
        if (Files.notExists(filePath)) {
            throw new BinaryContentNotFoundException(id);
        }
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일을 읽을 수 없습니다.", e);
        }
    }

    // 파일 다운로드
    @Override
    public ResponseEntity<Resource> download(BinaryContentResponseDto metadata) {
            log.debug("다운로드를 시작합니다.");
            InputStream inputStream = get(metadata.id()); // InputStream: 바이트 데이터를 읽기 위한 표준 통로
            Resource resource = new InputStreamResource(inputStream); // Resource: 추상화된 '자원'. 파일, URL 등..

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + metadata.fileName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, metadata.contentType())
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metadata.size()))
                    .body(resource);

    }

    // 저장위치 규칙 정의
    Path resolvePath(UUID id){
        return this.root.resolve(id.toString());
    }

}
