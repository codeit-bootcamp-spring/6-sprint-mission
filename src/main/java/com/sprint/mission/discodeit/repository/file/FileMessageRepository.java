package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        if (message.getId() == null) {
            message.setId(UUID.randomUUID());
        }
        Path path = resolvePath(message.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message messageNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                messageNullable = (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(messageNullable);
    }

    @Override
    public List<Message> findAll() {
        try {
            List<Message> allMessages = new ArrayList<>();
            for (Path path : Files.list(DIRECTORY).toList()) {
                if (path.toString().endsWith(EXTENSION)) {
                    try (
                            FileInputStream fis = new FileInputStream(path.toFile());
                            ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                        allMessages.add((Message) ois.readObject());
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return allMessages;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : this.findAll()) {
            if (channelId.equals(message.getChannelId())) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public Optional<Instant> findLatestMessageTimestamp(UUID channelId) {
        Instant latestTime = null;
        for (Message message : this.findAllByChannelId(channelId)) {
            Instant createdAt = Instant.ofEpochSecond(message.getCreatedAt());
            if (latestTime == null || createdAt.isAfter(latestTime)) {
                latestTime = createdAt;
            }
        }
        return Optional.ofNullable(latestTime);
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<Message> messagesToDelete = findAllByChannelId(channelId);
        for (Message message : messagesToDelete) {
            try {
                Files.deleteIfExists(resolvePath(message.getId()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}