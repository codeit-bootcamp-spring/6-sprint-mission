package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Channel implements Serializable {
    private String name;
    private String manager;
    private List<User> userList;
    private Common common;
    @Serial
    private static final long serialVersionUID = 1L;

    public Channel(String name) {
        this.name = name;
        this.common = new Common();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(User user) {
        this.manager = user.getName();
    }

    public void setUserList(User user){
        this.userList.add(user);
    }

    public Common getCommon() {
        return common;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
                ", manager='" + manager + '\'' +
                ", userList=" + userList +
                ", channelId=" + common.getId() +
                '}';
    }
}
