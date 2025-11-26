package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.file.FileDownloadException;
import com.sprint.mission.discodeit.exception.file.FileInPutException;
import com.sprint.mission.discodeit.exception.file.FileOutPutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path root) {
        this.root = root;
        init();
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    void init() {
        System.out.println(root.toString());
        File directory = new File(root.toString());
        if (!directory.exists()) {
            // 경로가 존재하지 않을 때
            boolean ok = directory.mkdirs();
            if (!ok) {
                System.out.println("디랙토리 생성 안됨");
            }
        }
    }

    @Override
    public UUID put(UUID id, byte[] data) {
        Path path = resolvePath(id);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());) {
            fos.write(data);
        } catch (IOException e) {
            log.error("파일 저장 오류 발생: 파일Id={}",id);
            throw new FileOutPutException();
        }
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        InputStream stream = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try {
                stream = new FileInputStream(path.toFile());
            } catch (IOException e) {
                log.error("파일 읽어오기 오류 발생: 파일Id={}",id);
                throw new FileInPutException();
            }
        }
        return stream;
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
        try (InputStream stream = get(binaryContentDto.id())){

            byte[] bytes = stream.readAllBytes();
            String encodedFileName = URLEncoder.encode(
                    binaryContentDto.fileName(),
                    StandardCharsets.UTF_8
            ).replace("\\+", "%20");

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
                    .contentLength(binaryContentDto.size())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .body(bytes);
        } catch (IOException e) {
            log.error("파일 다운로드 오류 발생: 파일 이름={}",binaryContentDto.fileName());
            throw new FileDownloadException();
        }
    }
}
