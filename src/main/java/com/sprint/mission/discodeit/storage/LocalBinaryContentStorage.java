package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "discodeit.storage",
    name = "type",
    havingValue = "local"
)
public class LocalBinaryContentStorage implements BinaryContentStorage {

  @Value("${discodeit.storage.root-path}")
  private String fileDirectory;

  private Path root;

  // @Value 필드값은 Bean 생성 이후에 채워짐, 인스턴스 필드 초기화 시점을 Bean 생성 이후로 미뤄 NullPointerException 방지
  @PostConstruct
  public void init() {
    this.root = Paths.get(System.getProperty("user.dir"), this.fileDirectory);

    if (!Files.exists(this.root)) {
      try {
        Files.createDirectories(this.root);
      } catch (IOException e) {
        throw new RuntimeException("로컬 파일 저장소 초기화 실패");
      }
    }
  }

  private Path resolvePath(UUID id) {
    return this.root.resolve(id.toString());
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] data) {
    Path filePath = resolvePath(binaryContentId);
    try {
      Files.write(filePath, data);
      return binaryContentId;
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패");
    }
  }

  @Override
  public InputStream get(UUID binaryContentId) throws IOException {
    Path filePath = resolvePath(binaryContentId);
    return Files.newInputStream(filePath);
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

  public void delete(UUID binaryContentId) {
    Path filePath = resolvePath(binaryContentId);
    try {
      Files.deleteIfExists(filePath);
    } catch (IOException e) {
      throw new RuntimeException("파일 삭제 실패");
    }
  }

  public void deleteAllByIdIn(List<UUID> binaryContentIdList) {
    for (UUID binaryContentId : binaryContentIdList) {
      delete(binaryContentId);
    }
  }

}
