package com.sprint.mission.discodeit.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserLogInOutEvent {
    String id;
    boolean loggedIn;
}
