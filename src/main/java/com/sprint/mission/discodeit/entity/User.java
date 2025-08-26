package com.sprint.mission.discodeit.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private final UUID id; // 유저 아이디
    private final long createdAt; // 유저 생성 시각
    private long updatedAt; // 유저 수정 시각

    private String name; // 유저 이름
    private String status; // 유저 상태
    private String email; // 유저 이메일

    //생성자 (유저명, 유저 상태, 유저 이메일)
    public User(String name, String status, String email){
        this.id = UUID.randomUUID(); // 유저 아이디 난수 생성
        this.createdAt = System.currentTimeMillis() / 1000L; // 생성 시각

        this.name = name; // 유저 이름 초기화
        this.status = status; // 유저 상태 초기화
        this.email = email; // 유저 이메일 초기화
    }


    //Getter
    public UUID getId(){return id;}
    public long getCreatedAt(){ return createdAt; }
    public long getUpdatedAt(){ return updatedAt; }
    public String getName(){ return name; }
    public String getStatus(){ return status; }
    public String getEmail(){ return email; }

    //update
    private void update(){ // 수정 시각  (외부에서 접근할 이유 X - private)
        this.updatedAt = System.currentTimeMillis() / 1000L;
    }
    public void setName(String updatedName){ // 유저 이름 업데이트
        this.name = updatedName;
        update();
    }
    public void setStatus(String updatedStatus){ // 유저 상태 업데이트
        this.status = updatedStatus;
        update();
    }
    public void setEmail(String updatedEmail){ // 이메일 업데이트
        this.email = updatedEmail;
        update();
    }

    public String toString(){
        return "[유저 이름: " + name + ", 유저 아이디: " + id +
                "\n상태: " + status + "\n이메일: " + email +
                "\n생성 시각: " + createdAt + ", 수정 시각: " + updatedAt + "]\n";
    }

}
