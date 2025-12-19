package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper<T> {

  public PageResponse<T> fromSlice(Slice<T> slice) {

    List<T> content = slice.getContent();
    boolean hasNext = slice.hasNext();

    Instant nextCursor = null;
    if (!content.isEmpty() && hasNext) {
      nextCursor = ((MessageDto) content.get(content.size() - 1)).createdAt();
    }

    return new PageResponse<>(
        content,
        nextCursor,
        slice.getSize(),
        hasNext,
        null
    );
  }

  public PageResponse<T> fromPage(Page<T> page) {
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }
}
