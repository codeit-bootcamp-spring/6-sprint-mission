package com.sprint.mission.discodeit.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagingDTO {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OffsetRequest {

    private int page;
    private int size;
    private String sort;

    public static OffsetRequest of(int page, int size) {
      return new OffsetRequest(page, size, null);
    }

    public static OffsetRequest of(int page, int size, String sort) {
      return new OffsetRequest(page, size, sort);
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OffsetPage<T> {

    private List<T> content;
    private int number;
    private int size;
    private boolean hasNext;
    private Long totalElement;

  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CursorRequest {

    private int size;
    private String sort;

    public static CursorRequest of(int size) {
      return new CursorRequest(size, null);
    }

    public static CursorRequest of(int size, String sort) {
      return new CursorRequest(size, "createdAt,desc");
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CursorPage<T> {

    private List<T> content;
    private T nextCursor;
    private int size;
    private boolean hasNext;
    private Long totalElement;

  }

}
