package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class Common implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id; // 변경되면 안됨
    private final Long createdAt; // 변경되면 안됨
    private Long updateAt;

    public Common(){
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = null;

    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public Long setUpdateAt(){
        return this.updateAt = System.currentTimeMillis();
    }

    // User, Channel, Message의 필드 하나씩 변곁
    protected <T> void setIfChanged(T currentValue, T newValue, Consumer<T> assign, String fieldName) {
        if (Objects.equals(currentValue, newValue)){
            throw new IllegalArgumentException(fieldName + " is not changed");
        }
        assign.accept(newValue);
        this.setUpdateAt();

    }

    /// ㅅㄷㄴㅅ
}
