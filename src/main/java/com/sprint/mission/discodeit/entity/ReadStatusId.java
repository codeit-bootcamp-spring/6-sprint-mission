package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusId implements Serializable {

    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID user;

    @Column(name = "channel_id", columnDefinition = "BINARY(16)")
    private UUID channel;

}