package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.enums.BinaryContentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
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
    @Enumerated(EnumType.STRING)
    private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;


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
        BinaryContent binaryContent = BinaryContent.builder()
                .fileName(fileName)
                .contentType(contentType)
                .size(size)
                .user(user)
                .build();
        binaryContent.status = BinaryContentStatus.PROCESSING;
        return binaryContent;
    }

    public static BinaryContent createAttachmentImage(String fileName, String contentType, Long size, Message message) {
        BinaryContent binaryContent = BinaryContent.builder()
                .fileName(fileName)
                .contentType(contentType)
                .size(size)
                .message(message)
                .build();
        binaryContent.status = BinaryContentStatus.PROCESSING;
        return binaryContent;
    }

    public void updateStatus(BinaryContentStatus newStatus) {
        this.status = newStatus;
    }
}

