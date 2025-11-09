package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.Page.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.time.Instant;

@Mapper(componentModel = "spring")
public class PageResponseMapper {

    public <T> PageResponse<T> fromSlice (Slice<T> slice, Instant instant) {
        return new PageResponse<>(
                slice.getContent(),
                instant,
                slice.getSize(),
                slice.hasNext(),
                slice.stream().count()
        );
    }
    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.hasContent(),
                page.getTotalElements()
        );
    }


}
