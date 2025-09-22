package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private final ConcurrentHashMap<UUID, ReadStatus> storage = new ConcurrentHashMap<>();

    @Override
    public ReadStatus save(ReadStatus entity) {
        if (entity.getId() == null) {  //아이디 없으면
            entity.setId(UUID.randomUUID());  //새로 생성
        }
        storage.put(entity.getId(), entity);  //아이디를 키로 엔티티 저장
        return entity;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));  //해당아이디 읽음상태 반환
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {  //두개 아이디 일치 확인
        for (ReadStatus readStatus : storage.values()) {
            if (readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId)) {
                return Optional.of(readStatus);  //읽음 상태 반환
            }
        }
        return Optional.empty();  //데이터 없으면 빈 옵셔널 반환
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus readStatus : storage.values()) {
            if (readStatus.getUserId().equals(userId)) {
                result.add(readStatus);
            }
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus readStatus : storage.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                result.add(readStatus);
            }
        }
        return result;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<UUID> idsToDelete = new ArrayList<>();
        for (ReadStatus readStatus : storage.values()) {
            if (readStatus.getUserId().equals(userId)) {
                idsToDelete.add(readStatus.getId());
            }
        }

        for (UUID id : idsToDelete) {
            storage.remove(id);
        }
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<UUID> idsToDelete = new ArrayList<>();
        for (ReadStatus readStatus : storage.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                idsToDelete.add(readStatus.getId());
            }
        }
        for (UUID id : idsToDelete) {
            storage.remove(id);
        }
    }

}