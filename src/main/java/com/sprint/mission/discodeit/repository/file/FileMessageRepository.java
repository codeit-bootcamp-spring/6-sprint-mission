package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
=======
>>>>>>> 박지훈
=======
=======
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path directory = Paths.get("./src/main/resources/MessagesDate");
    private final FileEdit instance = new FileEdit();

    private Path filePaths(UUID id) {return directory.resolve(id + ".ser");}
=======
=======
>>>>>>> 박지훈
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static final Path directory = Paths.get("./src/main/resources/MessagesDate");
    private static final FileEdit instance = new  FileEdit();

    private Path filePaths(Message message) {

        return directory.resolve(message.getMessageId().toString() + ".ser");
    }
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path directory = Paths.get("./src/main/resources/MessagesDate");
    private final FileEdit instance = new FileEdit();

    private Path filePaths(UUID id) {return directory.resolve(id + ".ser");}
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
    public FileMessageRepository() {
        instance.init(directory);
    }

<<<<<<< HEAD
<<<<<<< HEAD
    @Override
    public Message save(Message message) {
        instance.save(directory,message.getId(), message);
        return message;
    }
    @Override
    public Optional<Message> findById(UUID id) { return instance.load(directory,id); }

    @Override
    public List<Message> findAll() {
        return instance.loadAll(directory);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        boolean isDelete = instance.delete(directory,id);
        if(!isDelete){
            throw new NullPointerException(" 유저 삭제 실패");
=======
=======
>>>>>>> 박지훈
    public void createMessage(Message message) {
        instance.save(filePaths(message), message);
    }

    public Message findMessageById(UUID id) {
        List<Message> messageList = instance.load(directory);
        return messageList.stream()
                .filter(message -> message.getMessageId().equals(id))
                .findAny()
                .orElse(null);
    }

    public List<Message> findAllMessages() {
        return instance.load(directory);
    }

    public void updateMessage(Message message) {
        instance.save(filePaths(message), message);
    }

    public void deleteMessage(UUID id) {
        Message message = this.findMessageById(id);
        boolean isDelete = instance.delete(filePaths(message));
        if(!isDelete){
            throw new NullPointerException(message.getMessageId()+" 유저 삭제 실패");
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
    @Override
    public Message save(Message message) {
        instance.save(directory,message.getId(), message);
        return message;
    }
    @Override
    public Optional<Message> findById(UUID id) { return instance.load(directory,id); }

    @Override
    public List<Message> findAll() {
        return instance.loadAll(directory);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        boolean isDelete = instance.delete(directory,id);
        if(!isDelete){
            throw new NullPointerException(" 유저 삭제 실패");
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
        }
    }
}
