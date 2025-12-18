package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.enums.ContentType;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContentDTO {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BinaryContent {

    private UUID id;
    private Instant createdAt;
    private String fileName;
    private Long size;
    private ContentType contentType;
    private byte[] bytes;

  }

  @Builder
  public record BinaryContentCreateCommand(String fileName, byte[] data, ContentType contentType) {

  }

}
