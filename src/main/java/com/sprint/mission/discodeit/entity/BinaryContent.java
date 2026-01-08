package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enumtype.BinaryContentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@Table(name = "binary_contents")
public class BinaryContent extends BaseUpdatableEntity{

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false,length = 20)
    @Enumerated(EnumType.STRING)
    private BinaryContentStatus status = BinaryContentStatus.PROCESSING;


    //채널에 파일 업로드
    @Builder
    public BinaryContent(String fileName, Long size, String contentType) {
        this.fileName = fileName;
        this.size = size;
        this.contentType= contentType;
    }

    public void updateStatus(BinaryContentStatus status){
        this.status = status;
    }


}
