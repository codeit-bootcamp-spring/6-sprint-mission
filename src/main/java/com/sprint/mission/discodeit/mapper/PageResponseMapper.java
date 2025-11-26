package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public class PageResponseMapper {

    public static <T> PageResponse<T> fromSlice(Slice<T> slice, Object nextCursor) {
        return new PageResponse<>(
                slice.getContent(),
                nextCursor,
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    public static <T> PageResponse<T> fromPage(Page<T> page){
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }

}
