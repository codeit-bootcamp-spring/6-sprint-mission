package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final String PROFILE_IMG = "profile_img";  //프로필 이미지가 저장될 디렉토리 상수이름 정의
    private static final String MESSAGE_IMG = "message_img";
    private final Path profileDirectory;
    private final Path messageDirectory;

    public FileBinaryContentRepository() throws IOException {
        this.profileDirectory = Paths.get(PROFILE_IMG);  //프로필 경로 설정
        this.messageDirectory = Paths.get(MESSAGE_IMG);

        if (!Files.exists(this.profileDirectory)) {  //디렉토리가 없으면
            Files.createDirectories(this.profileDirectory);  //새로 생성
        }

        if (!Files.exists(this.messageDirectory)) {
            Files.createDirectories(this.messageDirectory);
        }
    }

    private Path getProfileFilePath(UUID id) {
        return profileDirectory.resolve(id.toString() + ".img");  //아이디 기반 새 디렉토리 생성
    }

    private Path getMessageFilePath(UUID id) {
        return messageDirectory.resolve(id.toString() + ".img");
    }


    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path binaryPath;

        if (binaryContent.getUserId() != null) {  //유저아이디가 있으면 경로설정
            binaryPath = getProfileFilePath(binaryContent.getId());
        } else if (binaryContent.getMessageId() != null) {
            binaryPath = getMessageFilePath(binaryContent.getId());
        } else {  //둘다없으면
            System.err.println("userId 또는 messageId가 필요합니다.");
            return null;
        }

        try {
            Files.write(binaryPath, binaryContent.getData()); // 바이너리 데이터 파일에 쓰기
            return binaryContent;  //성공하면 객체 반환
        } catch (IOException e) {
            System.err.println("내용 저장 실패!! " + binaryContent.getId() + " - " + e.getMessage());
            return null;  //오류시 null반환
        }
    }

    @Override
    public List<BinaryContent> saveAll(List<BinaryContent> binaryContents) {
        List<BinaryContent> savedContents = new java.util.ArrayList<>();
        for (BinaryContent content : binaryContents) {
            BinaryContent saved = save(content);
            if (saved != null) {
                savedContents.add(saved);  //객체를 한번에 저장
            }
        }
        return savedContents;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path filePath = getProfileFilePath(id);
        if (Files.exists(filePath)) {
            try {
                byte[] data = Files.readAllBytes(filePath);
                BinaryContent content = new BinaryContent();
                content.setId(id);
                content.setData(data);
                return Optional.of(content);
            } catch (IOException e) {
                System.err.println("파일 읽기 실패!! " + id + " - " + e.getMessage());
            }
        }

        filePath = getMessageFilePath(id);
        if (Files.exists(filePath)) {
            try {
                byte[] data = Files.readAllBytes(filePath);
                BinaryContent content = new BinaryContent();
                content.setId(id);
                content.setData(data);
                return Optional.of(content);
            } catch (IOException e) {
                System.err.println("파일 읽기 실패!! " + id + " - " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public Optional<BinaryContent> findByMessageId(UUID messageId) {
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(getProfileFilePath(id));
            Files.deleteIfExists(getMessageFilePath(id));
        } catch (IOException e) {
            System.err.println("파일 지우기 실패!! " + id + " - " + e.getMessage());
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        findByUserId(userId).ifPresent(content -> deleteById(content.getId()));
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        findByMessageId(messageId).ifPresent(content -> deleteById(content.getId()));
    }
}