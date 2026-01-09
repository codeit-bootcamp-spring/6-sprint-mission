package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class Notification extends BaseEntity{

    @Column(nullable = false)
    UUID receiverId;

    @Column(nullable = false,length = 100)
    String title;

    @Column(nullable = false)
    String content;

    @Builder
    public Notification(UUID receiverId, String title, String content){
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
    }
}
