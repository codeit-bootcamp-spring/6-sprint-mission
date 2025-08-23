package com.sprint.mission.discodeit.entity;


import java.util.UUID;
import java.io.Serializable;
import java.util.Objects;


public class User implements Serializable  {
    private static final long serialVersionUID = 1L;

    private UUID Id;
    private String name;
    private long createAt;
    private long updateAt;

    //Id생성자는 서비스에서 할당, 초기null 또는 자동생성
    public User(String name, long createAt, long updateAt ) {
        this.name = name;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    // --- Getters ---
    public UUID getId() { return Id; }
    public String getName() { return name; }


    // --- Setters (주로 서비스 레이어에서 ID 할당 또는 사용자 정보 수정 시 사용) ---
    public void setId(UUID id) { this.Id = Id; }
    public void setName(String name) { this.name = name; }


    // --- Utility Methods ---
    @Override
    public String toString() {
        // UUID는 너무 길어 일부만 표시하여 가독성 높임
        return "User{id=" + (Id != null ? Id.toString().substring(0, 8) + "..." : "null")
                + ", name='" + name + "'}";
    }

    // equals와 hashCode는 객체 비교에 사용되므로, ID를 기준으로 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(Id, user.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}


