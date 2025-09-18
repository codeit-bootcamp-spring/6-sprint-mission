package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
<<<<<<< HEAD
import com.sprint.mission.discodeit.DTO.User.CreateUserDTO;
import com.sprint.mission.discodeit.DTO.User.UpdateUserDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant created;
    private final int age;

    private UUID profileId;
    private Instant updated;
    private String password;
    private String username;
    private String email;

    public User(CreateUserDTO createUserDTO) {
        this.id = UUID.randomUUID();
        this.profileId = createUserDTO.profileId() == null ? UUID.randomUUID() : profileId;
        this.created = setTime();
        this.username = createUserDTO.userName();
        this.age = createUserDTO.age();
        this.email = createUserDTO.email();
        this.password = createUserDTO.password();
    }


    public void update(UpdateUserDTO updateUserDTO) {
        boolean anyValueUpdated = false;
        if (updateUserDTO.userName() != null && !updateUserDTO.userName().equals(this.username)) {
            this.username = updateUserDTO.userName();
            anyValueUpdated = true;
        }

        if (updateUserDTO.email() != null && !updateUserDTO.email().equals(this.email)) {
            this.email = updateUserDTO.email();
            anyValueUpdated = true;
        }

        if (updateUserDTO.password() != null && !updateUserDTO.password().equals(this.password)) {
            this.password = updateUserDTO.password();
            anyValueUpdated = true;
        }

        if (updateUserDTO.profileId()!= null && !updateUserDTO.profileId().equals(this.profileId)) {
            this.profileId = updateUserDTO.profileId();
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updated = setTime();
        }
    }


    //유져 본연에 속성이 변경 시에만 업데이트 갱신
    @Override
=======
=======
>>>>>>> 박지훈
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private String username;
    private final int age;
    private String email;
    private List<User> friends;
    private List<Channel>  channels;
    private final Long created;
    private Long updated;

    public User(String username, int age, String email) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.age = age;
        this.email = email;
        this.friends = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.created = setTime();
    }

    //Getter 생성
    public UUID getUserId() {return this.id;}
    public String getUsername() {return this.username;}
    public int getAge() {return this.age;}
    public String getEmail() {return this.email;}
    public List<User> getFriends() {return this.friends;}
    public List<Channel> getChannels() {return this.channels;}
    public Long getCreated() {return this.created;}
    public Long getUpdated() {return this.updated;}

    //update
    public void updateedUsername(String username) {
        this.username = username;
        this.updated =setTime();
    }

    public  void updateEmail(String email) {
        this.email = email;
        this.updated =setTime();
    }

    //유져 본연에 속성이 변경 시에만 업데이트 갱신
    public void updatedFriends(User friend) {
        this.friends.add(friend);
    }

    public void updatedChannels(Channel channel) {
        this.channels.add(channel);
    }

<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import com.sprint.mission.discodeit.DTO.User.CreateUserDTO;
import com.sprint.mission.discodeit.DTO.User.UpdateUserDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant created;
    private final int age;

    private UUID profileId;
    private Instant updated;
    private String password;
    private String username;
    private String email;

    public User(CreateUserDTO createUserDTO) {
        this.id = UUID.randomUUID();
        this.profileId = createUserDTO.profileId() == null ? UUID.randomUUID() : profileId;
        this.created = setTime();
        this.username = createUserDTO.userName();
        this.age = createUserDTO.age();
        this.email = createUserDTO.email();
        this.password = createUserDTO.password();
    }


    public void update(UpdateUserDTO updateUserDTO) {
        boolean anyValueUpdated = false;
        if (updateUserDTO.userName() != null && !updateUserDTO.userName().equals(this.username)) {
            this.username = updateUserDTO.userName();
            anyValueUpdated = true;
        }

        if (updateUserDTO.email() != null && !updateUserDTO.email().equals(this.email)) {
            this.email = updateUserDTO.email();
            anyValueUpdated = true;
        }

        if (updateUserDTO.password() != null && !updateUserDTO.password().equals(this.password)) {
            this.password = updateUserDTO.password();
            anyValueUpdated = true;
        }

        if (updateUserDTO.profileId()!= null && !updateUserDTO.profileId().equals(this.profileId)) {
            this.profileId = updateUserDTO.profileId();
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updated = setTime();
        }
    }


    //유져 본연에 속성이 변경 시에만 업데이트 갱신
    @Override
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
    public String toString(){
        return "유저 정보: "+ "\n" +
                "ID: " + this.id + "\n" +
                "아름: " + this.username + "\n" +
                "나이: " + this.age + "\n" +
                "이메일: " + this.email + "\n" +
                "계정 생성일자: " + this.created + "\n" +
<<<<<<< HEAD
<<<<<<< HEAD
=======
                "친구: " + this.friends + "\n" +
                "입장 채널: " + this.channels + "\n" +
>>>>>>> 박지훈
=======
                "친구: " + this.friends + "\n" +
                "입장 채널: " + this.channels + "\n" +
=======
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
                "계정 생성일자: " + this.updated + "\n";
    }
}
