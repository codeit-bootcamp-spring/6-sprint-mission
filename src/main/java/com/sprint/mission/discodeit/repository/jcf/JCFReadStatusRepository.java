package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final ConcurrentHashMap<UUID, ReadStatus> storage = new ConcurrentHashMap<>();

    @Override
    public ReadStatus save(ReadStatus entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus rs : storage.values()) {
            if (rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId)) {
                return Optional.of(rs);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : storage.values()) {
            if (rs.getUserId().equals(userId)) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : storage.values()) {
            if (rs.getChannelId().equals(channelId)) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<UUID> idsToDelete = new ArrayList<>();
        for (ReadStatus rs : storage.values()) {
            if (rs.getUserId().equals(userId)) {
                idsToDelete.add(rs.getId());
            }
        }
        idsToDelete.forEach(storage::remove);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<UUID> idsToDelete = new ArrayList<>();
        for (ReadStatus rs : storage.values()) {
            if (rs.getChannelId().equals(channelId)) {
                idsToDelete.add(rs.getId());
            }
        }
        idsToDelete.forEach(storage::remove);
    }

}
