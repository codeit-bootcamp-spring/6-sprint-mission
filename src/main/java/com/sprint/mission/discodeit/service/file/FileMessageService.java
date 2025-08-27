package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageRepository, Serializable {
    private static final long serialVersionUID = 1L;

    private List<Message> messages = new ArrayList<>();
    private Long nextId = 1L; // 자동 증가 ID

    public FileMessageService() {
        // 기본 생성자
    }

    // 역직렬화 시 nextId를 재조정합니다.
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // 기본 역직렬화 수행
        this.nextId = messages.stream()
                .mapToLong(Message::getId)
                .max()
                .orElse(0L) + 1; // 최대값이 없으면 (리스트 비어있으면) 1부터 시작
        System.out.println("MessageSvc: 역직렬화 후 nextId 재설정됨 -> " + nextId);
    }

    @Override
    public Message createMessage(Message message) {
        message.setId(nextId++); // 현재 nextId 값을 할당하고 1 증가
        messages.add(message);
        // 메시지 내용이 길 경우 잘라내어 미리보기로 표시
        String preview = message.getContent().length() > 20 ? message.getContent().substring(0, 20) + "..." : message.getContent();
        System.out.println("MessageSvc: 새 메시지 등록 -> '" + preview + "' (ID: " + message.getId() + ")");
        return message;
    }

    @Override
    public Optional<Message> readMessage(Long Id) {
        return messages.stream()
                .filter(msg -> msg.getId().equals(Id))
                .findFirst();
    }

    @Override
    public List<Message> readAllMessages() {
        return new ArrayList<>(messages);
    }

    @Override
    public List<Message> readMessagesByChannelId(Long channelId) {
        return messages.stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> readMessagesBySenderUserId(UUID senderUserId) {
        return messages.stream()
                .filter(msg -> msg.getSenderUserId().equals(senderUserId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> updateMessage(Message message) {
        Optional<Message> existingMessageOpt = readMessage(message.getId());
        if (existingMessageOpt.isPresent()) {
            Message existingMessage = existingMessageOpt.get();
            existingMessage.setContent(message.getContent()); // 메시지 내용만 수정 가능
            String preview = message.getContent().length() > 20 ? message.getContent().substring(0, 20) + "..." : message.getContent();
            System.out.println("MessageSvc: 메시지 수정 -> '" + preview + "' (ID: " + message.getId() + ")");
            return Optional.of(existingMessage);
        } else {
            System.out.println("MessageSvc: ID " + message.getId() + " 에 해당하는 메시지 없음. 수정 실패.");
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteMessage(Long Id) {
        boolean removed = messages.removeIf(msg -> msg.getId().equals(Id));
        if (removed) {
            System.out.println("MessageSvc: 메시지 ID " + Id + " 삭제 완료.");
        } else {
            System.out.println("MessageSvc: 메시지 ID " + Id + " 를 찾을 수 없어 삭제 실패.");
        }
        return removed;
    }
}
