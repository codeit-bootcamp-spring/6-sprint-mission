package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepository {

    Path directory = Paths.get(System.getProperty("user.dir"), "MessageData");

    @Override
    public Message find(UUID id) {
        return findall()
                .stream()
                .filter(msg->msg.getCommon().getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public Message save(Message message) {
        FileInitSaveLoad.init(directory);

        Path filePath = directory.resolve(message.getCommon().getId()+".ser");
        FileInitSaveLoad.<Message>save(filePath, message);

        return message;
    }

    @Override
    public List<Message> findall() {
        return FileInitSaveLoad.<Message>load(directory);
    }

    @Override
    public void delete(UUID id) {
        Message message = findall()
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
