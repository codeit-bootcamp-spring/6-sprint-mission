package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long Id; // 채널 ID는 Long으로 자동 증가 관리
    private String name;
    private LocalDateTime createdAt;

    // 생성자: ID는 서비스에서 할당
    public Channel(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now(); // 생성 시 현재 시간 자동 설정
    }

    // --- Getters ---
    public Long getId() { return Id; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // --- Setters ---
    public void setId(Long id) { this.Id = Id; }
    public void setName(String name) { this.name = name; }

    // --- Utility Methods ---
    @Override
    public String toString() {
        return "Channel{Id=" + Id + ", name='" + name + "', createdAt=" + createdAt + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(Id, channel.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}