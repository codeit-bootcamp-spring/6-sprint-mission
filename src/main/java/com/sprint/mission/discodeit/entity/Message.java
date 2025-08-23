package com.sprint.mission.discodeit.entity;


import java.util.UUID;
import java.io.Serializable;
import java.util.Objects;


public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long Id;  //long 으로 자동 증가 관리
    private UUID senderUserId;
    private Long channelId;
    private String content;
    private long createAt;
    private long updateAt;

    public Message(UUID senderUserId, Long channelId, String content) {
        this.senderUserId = senderUserId;
        this.channelId = channelId;
        this.content = content;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    // --- Getters ---
    public Long getId() { return Id; }
    public UUID getSenderUserId() { return senderUserId; }
    public Long getChannelId() { return channelId; }
    public String getContent() { return content; }


    // --- Setters ---
    public void setId(Long id) { this.Id = Id; }
    public void setContent(String content) { this.content = content; } // 메시지 내용은 수정 가능

    // --- Utility Methods ---
    @Override
    public String toString() {
        // UUID는 너무 길어 일부만 표시, 가독성 높임
        String senderIdShort = (senderUserId != null) ? senderUserId.toString().substring(0, 8) + "..." : "null";
        return "Message{id=" + Id + ", senderUserId=" + senderIdShort + ", channelId=" + channelId +
                ", content='" + content + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(Id, message.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}
