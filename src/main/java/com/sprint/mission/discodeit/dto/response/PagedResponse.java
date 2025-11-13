package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int number,
        int size,
        boolean hasNext,
        Long totalElements
) {
    public static <T> PagedResponse<T> of(List<T> content, int number, int size, boolean hasNext, Long totalElements) {
        return new PagedResponse<>(content, number, size, hasNext, totalElements);
    }

    public static <T> PagedResponse<T> of(List<T> content, int number, int size, boolean hasNext) {
        return new PagedResponse<>(content, number, size, hasNext, null);
    }
}