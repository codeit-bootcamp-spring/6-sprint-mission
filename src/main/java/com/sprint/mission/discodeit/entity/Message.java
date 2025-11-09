package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@Table(name = "messages")
@NoArgsConstructor
public class Message extends BaseUpdatableEntity {

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User author;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments;

    public Message(User author, Channel channel, String content) {
        this.channel = channel;
        this.author = author;
        this.content = content;
        this.attachments = new ArrayList<>();
    }

    @Builder
    public Message(User author, Channel channel, String content, List<BinaryContent> attachment) {
        this.channel = channel;
        this.author = author;
        this.content = content;
        this.attachments = new ArrayList<>(attachment);
    }


    public void update(String content) {
        boolean anyValueUpdated = false;
        if (content != null && !content.equals(this.content)) {
            this.content = content;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updatedAtNow();
        }
    }

}
