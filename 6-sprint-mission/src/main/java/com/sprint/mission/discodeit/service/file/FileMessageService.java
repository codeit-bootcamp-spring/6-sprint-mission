package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    Path directory = Paths.get(System.getProperty("user.dir"), "MessageData");

    @Override
    public Message create(User sender, User reciever, String content, Channel channel) {
        FileInitSaveLoad.init(directory);

        Message message = new Message(sender, reciever, content, channel);

        Path filePath = directory.resolve(message.getCommon().getId()+".ser");
        FileInitSaveLoad.<Message>save(filePath, message);

        return message;
    }

    @Override
    public Message read(String sender) {
        return allRead()
                .stream()
                .filter(msg -> msg.getSender().equals(sender))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Message> allRead() {
        return FileInitSaveLoad.<Message>load(directory);
    }

    @Override
    public Message modify(UUID id, String content) {
        Message message = allRead()
                .stream()
                .filter(msg -> msg.getCommon().getId().equals(id))
                .findAny()
                .orElse(null);
        if (message != null) {
            message.setContent(content);
            Path filePath = directory.resolve(message.getCommon().getId()+".ser");         // 수정된 이름의 객체가 더 생길수있기에 파일명을 id.ser로 해야함
            FileInitSaveLoad.<Message>save(filePath, message);        // 덮어쓰기
            return message;
        } else {
            System.out.println("해당 메시지 없음");
            return null;
        }
    }

    @Override
    public void delete(UUID id) {
        Message message = allRead()
                .stream()
                .filter(msg -> msg.getCommon().getId().equals(id))
                .findAny()
                .orElse(null);
        File file = new File("MessageData/"+message.getCommon().getId()+".ser");
        if(file.delete()){
            //System.out.println("---delete---");
        }
    }
}
