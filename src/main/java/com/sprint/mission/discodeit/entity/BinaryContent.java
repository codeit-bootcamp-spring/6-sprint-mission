package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private Long size;
    @Column(length = 100, nullable = false)
    private String contentType;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BinaryContentStatus status;

    public BinaryContent(String fileName, Long size, String contentType) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.status = BinaryContentStatus.PROCESSING;
    }

    public void updateStatus(BinaryContentStatus status) {
        if (status != null && !this.status.equals(status)) {
            this.status = status;
        }
    }
}
