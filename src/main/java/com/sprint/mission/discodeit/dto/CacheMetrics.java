package com.sprint.mission.discodeit.dto;

import lombok.Builder;

@Builder
public record CacheMetrics (
        long size,
        long hitCount,
        long missCount,
        String hitRate
){

}
