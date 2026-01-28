package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.enums.ChannelType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "channels")
@EntityListeners(AuditingEntityListener.class)
public class Channel extends BaseUpdatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @OneToMany(mappedBy = "channel")
    private List<Message> messages;

    @OneToMany(mappedBy = "channel")
    private List<ReadStatus> readStatuses;

    @Column(nullable = false)
    private String name;

    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Channel (ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public static Channel createPublicChannel(String name, String description) {
        return Channel.builder()
                .type(ChannelType.PUBLIC)
                .name(name)
                .description(description)
                .build();
    }

    public static Channel createPrivateChannel(){
        return Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();
    }

    public void updatePublicChannel(String newName, String newDescription) {
        if (newName != null){
            this.name = newName;
        }
        if (newDescription != null){
            this.description = newDescription;
        }
    }

    public boolean isPrivate(){
        return (type == ChannelType.PRIVATE);
    }
}
