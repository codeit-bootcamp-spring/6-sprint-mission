package com.sprint.mission.discodeit.dto.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.enums.ContentType;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContentResponseDTO {

  @Builder
  public record ReadBinaryContentResponse(
      UUID id,
      String fileName,
      Long size,
      @JsonProperty("contentType")
      ContentType contentType
  ) {

  }
}
