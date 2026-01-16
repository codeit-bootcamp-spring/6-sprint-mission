package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

/**
프사 또는 첨부파일
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, updatable = false)
    private String fileName;

    @Column(nullable = false, updatable = false)
    private String contentType;

    @Column(nullable = false, updatable = false)
    private Long size;

    @Column(nullable = false)
    private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // 파일 저장 상태
    public enum BinaryContentStatus {
        PROCESSING,
        SUCCESS,
        FAIL
    }

    @Builder(access = AccessLevel.PRIVATE)
    public BinaryContent(Message message, User user, String fileName, String contentType, Long size, BinaryContentStatus status) {
        this.message = message;
        this.user = user;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.status = status;
    }

    public static BinaryContent createProfileImage(String fileName, String contentType, Long size, User user) {
        return BinaryContent.builder()
                .fileName(fileName)
                .contentType(contentType)
                .size(size)
                .user(user)
                .build();
    }

    public static BinaryContent createAttachmentImage(String fileName, String contentType, Long size, Message message) {
        return BinaryContent.builder()
                .fileName(fileName)
                .contentType(contentType)
                .size(size)
                .message(message)
                .build();
    }

    public void updateStatus(BinaryContentStatus newStatus) {
        this.status = newStatus;
    }
}

