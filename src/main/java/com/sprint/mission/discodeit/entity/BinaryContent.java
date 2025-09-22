package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String contentType;
    private String filename;
    private byte[] data;
    private Instant createdAt;
    private UUID userId;
    private UUID messageId;

    public BinaryContent(UUID userId, String contentType, String filename) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.messageId = messageId;
        this.contentType = contentType;
        this.filename = filename;
        this.data = data;
    }
}