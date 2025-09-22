package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReadStatus {
    private UUID id;
    private Instant createdAt;
    private Instant lastReadAt;
    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.lastReadAt = Instant.now();
        this.userId = userId;
        this.channelId = channelId;
    }

    public void lastReadTime() {
        this.lastReadAt = Instant.now();
    }
}