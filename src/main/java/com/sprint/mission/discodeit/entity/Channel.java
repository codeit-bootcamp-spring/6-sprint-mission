package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.enums.ChannelType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@Table(name = "channels")
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
    @Builder.Default
    private Instant createdAt = Instant.now();;

    @LastModifiedDate
    private Instant updatedAt;
}
