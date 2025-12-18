package com.sprint.mission.discodeit.dto.api.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagingRequestDTO {

  @Builder
  public record OffsetRequest(
      @PositiveOrZero(message = "올바르지 않은 페이지 크기 형식입니다.")
      int size,
      @PositiveOrZero(message = "올바르지 않은 페이지 번호 형식입니다.")
      int page,
      String sort
  ) {

  }

  @Builder
  public record CursorRequest(
      @PositiveOrZero(message = "올바르지 않은 페이지 크기 형식입니다.")
      int size,
      String sort
  ) {

  }
}
