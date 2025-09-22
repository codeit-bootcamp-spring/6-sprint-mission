package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

    private static final String USER_STATUS = "user_statuses";  //파일저장 디렉토리 상수이름 정의
    private final Path directory;
    private final String EXTENSION = ".ser";  //확장자 명 정의

    public FileUserStatusRepository() throws IOException {
        this.directory = Paths.get(System.getProperty("user.dir"), USER_STATUS);  //해당 작업위치에 디렉토리 경로설정

        if (!Files.exists(this.directory)) {  //경로 없으면
            Files.createDirectories(this.directory);  //새로 생성
        }
    }

    @Override
    public UserStatus save(UserStatus entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());  //엔티티에 아이디 없으면 새로생성
        }

        Path path = directory.resolve(entity.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(entity);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패!!", e);
        }
        return entity;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path path = directory.resolve(id + EXTENSION);
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile());
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                return Optional.ofNullable((UserStatus) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("불러오기 실패!!", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        try (Stream<Path> files = Files.list(this.directory)) {
            for (Path file : files.toList()) {
                if (file.toString().endsWith(EXTENSION)) {
                    try (FileInputStream fis = new FileInputStream(file.toFile());
                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                        UserStatus userStatus = (UserStatus) ois.readObject();
                        if (userStatus.getUserId().equals(userId)) {
                            return Optional.of(userStatus);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("읽기 실패!!" + file.getFileName());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("디렉토리에서 읽기 실패!!" + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        List<UserStatus> allStatuses = new ArrayList<>();
        try (Stream<Path> files = Files.list(this.directory)) {
            for (Path file : files.toList()) {
                if (file.toString().endsWith(EXTENSION)) {
                    try (FileInputStream fis = new FileInputStream(file.toFile());
                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                        allStatuses.add((UserStatus) ois.readObject());
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("읽기 실패!!" + file.getFileName());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("디렉토리에서 읽기 실패!!" + e.getMessage());
        }
        return allStatuses;
    }

    @Override
    public void deleteById(UUID id) {
        Path userFile = this.directory.resolve(id.toString() + EXTENSION);
        try {
            Files.deleteIfExists(userFile);
        } catch (IOException e) {
            throw new RuntimeException("삭제 실패!!", e);
        }
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
        if (optionalUserStatus.isEmpty()) {
            return false;
        }

        UserStatus userStatus = optionalUserStatus.get();
        Instant cutoffTime = Instant.now().minus(minutesToConsiderOnline, ChronoUnit.MINUTES);

        return userStatus.getUpdatedAt() != null && userStatus.getUpdatedAt().isAfter(cutoffTime);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Optional<UserStatus> optionalUserStatus = findByUserId(userId);
        if (optionalUserStatus.isPresent()) {
            deleteById(optionalUserStatus.get().getId());
        }
    }

}