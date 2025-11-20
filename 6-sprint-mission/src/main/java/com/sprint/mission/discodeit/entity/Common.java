package com.sprint.mission.discodeit.entity;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Common implements Serializable {
    private UUID id;
    private Long createAt, updateAt;
    @Serial
    private static final long serialVersionUID = 1L;

    public Common() {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }
}
