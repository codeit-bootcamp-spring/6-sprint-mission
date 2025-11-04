package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage storage;

  @Override
  public User create(CreateUserRequest request, Optional<MultipartFile> profile) {
    User user;
    User userByUserName = userRepository.findByUsername(request.username()).orElse(null);
    User userByEmail = userRepository.findByEmail(request.email()).orElse(null);
    if (userByUserName != null || userByEmail != null) {
      throw new IllegalArgumentException("유저 이름 혹은 이메일이 같은 유저가 존재합니다.");
    }

    Optional<BinaryContent> binaryContentOptional = profile.map(
        file -> {
          try {
            BinaryContent bc = new BinaryContent(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
            );
            System.out.println(bc.getId() + " bc의 id");       // 여기 id는 null
            BinaryContent saved = binaryContentRepository.save(bc);
            storage.put(saved.getId(), file.getBytes());      // id는 영속화 이후 발생
            return saved;
          } catch (IOException e) {
            log.error("유저 프로필 사진 처리 실패", e);
            throw new RuntimeException("유저 프로필 사진 처리 실패");
          }
        }
    );
    user = new User(
        request.username(),
        request.email(),
        request.password()
    );
    UserStatus userStatus = new UserStatus(user, Instant.now());
    user.setUserStatus(userStatus);
    user.setProfile(binaryContentOptional.orElse(null));

    User saved = userRepository.save(user);

    log.info("유저 생성: id={}", saved.getId());
    if (saved.getProfile() != null) {
      log.info("프로필 사진 업로드: {}", saved.getProfile().getId());
    }
    log.debug("이름: {}, 이메일: {}", saved.getUsername(), saved.getEmail());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public User find(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("User not found. userId: {}", userId);
          return new UserNotFoundException(Map.of("유저 고유 아이디", userId));
        });
  }

  // 유저 목록 새로고침할때마다 상태 업데이트
  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    List<User> userList = userRepository.findAll();

    userList.forEach(user -> {
          userStatusRepository.findByUser_Id(user.getId())
              .ifPresent(userStatus -> user.update(userStatus.isOnline()));
          userRepository.save(user);
        }
    );

    return userList;
  }

  // 유저 이름, 이메일, 비밀번호, 사진, 온라인 상태 업데이트
  @Override
  public User update(UUID userId, UpdateUserRequest updateUserRequest,
      Optional<MultipartFile> profile) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("User not found. userId: {}", userId);
          return new UserNotFoundException(Map.of("유저 고유 아이디", userId));
        });
    user.update(updateUserRequest.newUsername(), updateUserRequest.newEmail(),
        updateUserRequest.newPassword());

    Optional<BinaryContent> binaryContent = profile.map(
        file -> {
          try {
            if (user.getProfile() == null) {
              BinaryContent bc = new BinaryContent(file.getOriginalFilename(), file.getSize(),
                  file.getContentType()
              );
              BinaryContent updated = binaryContentRepository.save(bc);
              storage.put(updated.getId(), file.getBytes());
              return updated;
            } else {
              user.getProfile()
                  .update(file.getOriginalFilename(), file.getSize(), file.getContentType());
              storage.put(user.getProfile().getId(), file.getBytes());     // 기존 프로필 사진 덮어쓰기
              return binaryContentRepository.save(user.getProfile());
            }
          } catch (IOException e) {
            throw new RuntimeException("이미지 가져오는데 실패");
          }
        }
    );
    user.update(binaryContent.orElse(null));

    UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
        .orElseThrow(() -> {
          log.warn("UserStatus not found. userId: {}", userId);
          return new UserNotFoundException(Map.of("유저 고유 아이디", userId));
        });
    userStatus.update(Instant.now());
    userStatusRepository.save(userStatus);

    User updated = userRepository.save(user);

    log.info("유저 정보 업데이트: id={}", updated.getId());
    if (updated.getProfile() != null) {
      log.info("프로필 사진 업로드: {}", updated.getProfile().getId());
    }
    log.debug("이름: {}, 이메일: {}", updated.getUsername(), updated.getEmail());
    return updated;
  }

  @Override
  public void delete(UUID userId) {
    if (!userRepository.existsById(userId)) {
      log.warn("User not found. userId: {}", userId);
      throw new UserNotFoundException(Map.of("유저 고유 아이디", userId));
    }
    // 유저의 프로필 사진 객체 삭제
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("User not found. userId: {}", userId);
          return new UserNotFoundException(Map.of("유저 고유 아이디", userId));
        });
    binaryContentRepository.deleteById(user.getProfile().getId());
    // 유저 상태들 삭제
    UserStatus userStatus = user.getUserStatus();
    userStatusRepository.deleteById(userStatus.getId());
    // 유저 id로 삭제
    userRepository.deleteById(userId);

    log.info("유저 삭제: id={}", userId);
  }
}
