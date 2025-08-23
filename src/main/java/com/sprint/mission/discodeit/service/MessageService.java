package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message ;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    void createMessage(Message message);  // 생성
    Message readMessage(String message);      // 단건 읽기
    List<Message> readAllMessages();   // 모두 읽기
    void updateMessage(Message message);  // 수정
    void deleteMessage(String message);    // 삭제
}