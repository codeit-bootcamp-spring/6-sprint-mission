package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@Primary
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        if (message.getId() == null) {
            message.setId(UUID.randomUUID());
        }
        this.data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : this.data.values()) {
            if (channelId.equals(message.getChannelId())) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public Optional<Instant> findLatestMessageTimestamp(UUID channelId) {
        Instant latestTime = null;
        for (Message msg : this.data.values()) {
            if (msg.getChannelId().equals(channelId)) {
                Instant createdAt = Instant.ofEpochSecond(msg.getCreatedAt());
                if (latestTime == null || createdAt.isAfter(latestTime)) {
                    latestTime = createdAt;
                }
            }
        }
        return Optional.ofNullable(latestTime);
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        Iterator<Map.Entry<UUID, Message>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Message> entry = iterator.next();
            if (entry.getValue().getChannelId().equals(channelId)) {
                iterator.remove();
            }
        }
    }

}