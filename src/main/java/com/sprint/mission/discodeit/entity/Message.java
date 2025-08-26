package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// 메시지
public class Message {
    private final UUID id; // 메시지 아이디
    private final long createdAt; // 메시지 생성 시각
    private long updatedAt; // 메시지 수정 시각

    private final UUID authorId; // 메시지 작성자 id
    private final UUID channelId; // 전송 채널 id
    private String authorName; // 메시지 작성자명
    private String channelName; // 전송 채널명
    private String content; //메시지 내용


    // 생성자 (전송자(작성자) id, 전송 채널 id, 메시지 내용)
    public Message(UUID authorId, UUID channelId,
                   String authorName, String channelName, String content){
        this.id = UUID.randomUUID(); // 메시지 아이디 난수 생성
        this.createdAt = System.currentTimeMillis() /1000L; // 생성 시각

        this.authorId= authorId; // 작성자 id 초기화
        this.channelId = channelId; // 전송 채널 id 초기화
        this.authorName = authorName; // 작성자명 초기화
        this.channelName = channelName; // 채널명 초기화
        this.content = content; // 메시지 내용 초기화
    }


    //Getter
    public UUID getId(){return id;}
    public long getCreatedAt(){ return createdAt; }
    public long getUpdatedAt(){ return updatedAt; }
    public UUID getAuthorId(){ return authorId; }
    public UUID getChannelId(){ return channelId; }
    public String getContent(){ return content; }


    //Update
    private void update(){ // 수정 시각 업데이트
        this.updatedAt = System.currentTimeMillis() / 1000L;
    }
    public void updateContent(String updatedContent){ // 메시지 내용 수정
        this.content = updatedContent;
        update();
    }
    public void updateAuthorName(String updatedAuthorName){ // 메시지 작성자명 수정
        this.authorName = updatedAuthorName;
        update();
    }
    public void updateChannelName(String updatedChannelName){
        this.channelName = updatedChannelName;
        update();
    }

    public String toString(){
        return "[메시지 아이디: " + id + "\n" +
                "작성자: " + authorName + ", 작성 채널: " + channelName + "\n" +
                content + "\n" +
                "생성 시각: " + createdAt + ", 수정 시각: " + updatedAt + "]";
    }


}
