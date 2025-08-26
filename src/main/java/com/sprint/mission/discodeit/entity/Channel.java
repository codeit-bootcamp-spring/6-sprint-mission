package com.sprint.mission.discodeit.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Channel {
    private final UUID id; // 채널 아이디
    private final long createdAt; // 채널 생성 시각
    private long updatedAt; // 채널 수정 시각

    private String name; // 채널명
    private String information; // 채널 정보


    //생성자 (채널명, 채널 정보)
    public Channel(String name, String information){
        this.id = UUID.randomUUID(); // 채널 아이디 난수 생성
        this.createdAt = System.currentTimeMillis() / 1000L; // 생성 시각

        this.name = name; // 채널명 초기화
        this. information = information; // 채널 정보 초기화
    }


    //Getter
    public UUID getId(){return id;}
    public long getCreatedAt(){ return createdAt; }
    public long getUpdatedAt(){ return updatedAt; }
    public String getName(){ return name; }
    public String getInformation(){ return information; }


    //update
    private void update(){ // 수정 시각 업데이트
        this.updatedAt = System.currentTimeMillis() / 1000L;
    }
    public void updateName(String updatedName){ // 채널명 업데이트
        this.name = updatedName;
        update();
    }
    public void updateInformation(String updatedInformation){ // 채널 정보 업데이트
        this.information = updatedInformation;
        update();
    }

    public String toString(){
        return "[채널명: " + name + ", 채널 아이디: " + id +
                "\n채널 정보: " + information +
                "\n생성 시각: " + createdAt + ", 수정 시각: " + updatedAt + "]\n";
    }
}
