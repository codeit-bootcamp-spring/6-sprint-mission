package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class JCFUserStatusRepository implements UserStatusRepository {

    private final ConcurrentHashMap<UUID, UserStatus> storage = new ConcurrentHashMap<>();

    @Override
    public UserStatus save(UserStatus entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        for (UserStatus userStatus : storage.values()) {
            if (userStatus.getUserId().equals(userId)) {
                return Optional.of(userStatus);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    @Override
    public void lastUpdateAt(UUID userId) {
        Optional<UserStatus> optionalUserStatus = findByUserId(userId);
        if (optionalUserStatus.isPresent()) {
            UserStatus userStatus = optionalUserStatus.get();
            userStatus.setUpdatedAt(Instant.now());
            save(userStatus);
        }
    }

    @Override
    public boolean isOnlineByUserId(UUID userId, long minutesToConsiderOnline) {
        Optional<UserStatus> optionalUserStatus = findByUserId(userId);
        if (optionalUserStatus.isPresent()) {
            UserStatus userStatus = optionalUserStatus.get();
            Instant cutoffTime = Instant.now().minus(minutesToConsiderOnline, ChronoUnit.MINUTES);
            return userStatus.getUpdatedAt().isAfter(cutoffTime);
        }
        return false;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Optional<UserStatus> optionalUserStatus = findByUserId(userId);
        if (optionalUserStatus.isPresent()) {
            storage.remove(optionalUserStatus.get().getId());
        }
    }

}