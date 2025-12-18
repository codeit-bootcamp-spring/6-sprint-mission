package com.sprint.mission.discodeit.dto.api.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagingResponseDTO {

  @Builder
  public record OffsetPageResponse<T>(
      List<T> content,
      int number,
      int size,
      boolean hasNext,
      Long totalElements
  ) {

  }

  @Builder
  public record CursorPageResponse<T>(
      List<T> content,
      T nextCursor,
      int size,
      boolean hasNext,
      Long totalElements
  ) {

  }
}
