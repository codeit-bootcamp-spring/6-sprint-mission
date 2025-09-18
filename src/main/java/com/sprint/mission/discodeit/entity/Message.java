package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
<<<<<<< HEAD
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final UUID sender;
    private final UUID channel;
    private final Instant created;

    private final List<UUID> attachmentIds;
    private String content;
    private Instant updated;

    public Message(UUID sender, UUID channel, String content) {
        this.channel = channel;
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.content = content;
        this.attachmentIds = new ArrayList<>();
        this.created = setTime();
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updated = setTime();
        }
    }


    public String toString(){
        return "메시지 정보" + "\n" +
                "메시지 ID: " + this.id + "\n" +
                "메시지 발신자 ID: " + this.sender + "\n" +
                "메시지 수신 체널 ID:  " + this.channel + "\n" +
                "메시지 내용: " + this.content + "\n" +
=======
=======
>>>>>>> 박지훈
import java.io.Serializable;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 2L;
    private final UUID id;
    private final User sender;
    private String text;
    private final Long created;
    private Long updated;

    public Message(User sender, String text) {
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.text = text;
        this.created = setTime();
    }

    public UUID getMessageId() {return this.id;}
    public User getMessageSender() {return this.sender;}
    public String getMessageText() {return this.text;}
    public Long getMessageCreated() {return this.created;}
    public Long getMessageUpdated() {return this.updated;}

    public void updateMessage(String text) {
        this.text = text;
        this.updated = setTime();
    }

    public String toString(){
        return "메시지 정보" + "\n" +
                "메시지 ID: " + this.id + "\n" +
                "메시지 발신자: " + this.sender.getUsername() + "\n" +
                "메시지 내용: " + this.text + "\n" +
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final UUID sender;
    private final UUID channel;
    private final Instant created;

    private final List<UUID> attachmentIds;
    private String content;
    private Instant updated;

    public Message(UUID sender, UUID channel, String content) {
        this.channel = channel;
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.content = content;
        this.attachmentIds = new ArrayList<>();
        this.created = setTime();
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updated = setTime();
        }
    }


    public String toString(){
        return "메시지 정보" + "\n" +
                "메시지 ID: " + this.id + "\n" +
                "메시지 발신자 ID: " + this.sender + "\n" +
                "메시지 수신 체널 ID:  " + this.channel + "\n" +
                "메시지 내용: " + this.content + "\n" +
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
                "메시지 생성일자: " + this.created + "\n" +
                "메시지 수정일자: " + this.updated + "\n";
    }
}
