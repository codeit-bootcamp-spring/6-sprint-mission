package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enumtype.ChannelType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Builder
    public Channel(String channelName, String description) {
        this.name = channelName;
        this.type = ChannelType.PUBLIC;
        this.description = description;
    }

    @Builder
    public Channel() {
        this.type = ChannelType.PRIVATE;
        this.name = "";
        this.description = "";
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAtNow();
        }
    }

}
