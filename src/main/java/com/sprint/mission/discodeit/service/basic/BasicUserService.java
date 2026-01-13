package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.Role;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;

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
          BinaryContent bc = new BinaryContent(
              file.getOriginalFilename(),
              file.getSize(),
              file.getContentType()
          );
          // em.persist(bc) 이후에 id 값이 생성됨
          BinaryContent saved = binaryContentRepository.save(bc);
          try {
            eventPublisher.publishEvent(new BinaryContentCreatedEvent(saved.getId(), file.getBytes()));
          } catch (IOException e) {
            log.error("유저 프로필 사진 처리 실패", e);
            throw new RuntimeException("유저 프로필 사진 처리 실패");
          }
          return saved;
        }
    );
    user = User.builder()
        .username(request.username())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .role(Role.USER)
        .build();
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
  @Cacheable(value = "userCache", key = "#userId", sync = true)
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
  @Cacheable(value = "userCache", key = "'all'", sync = true)
  @Transactional(readOnly = true)
  public List<User> findAll() {
    List<User> userList = userRepository.findAll();

    return userList;
  }

  // 유저 이름, 이메일, 비밀번호, 사진, 온라인 상태 업데이트
  @Override
  @CacheEvict(value = "userCache", key = "#userId")
  public User update(UUID userId, UpdateUserRequest updateUserRequest,
      Optional<MultipartFile> profile) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("User not found. userId: {}", userId);
          return new UserNotFoundException(Map.of("유저 고유 아이디", userId));
        });

    String rawNewPassword = updateUserRequest.newPassword();
    String encodedNewPassword = null;

    if (rawNewPassword != null && !passwordEncoder.matches(rawNewPassword, user.getPassword())) {
      encodedNewPassword = passwordEncoder.encode(rawNewPassword);
    }

    user.update(updateUserRequest.newUsername(), updateUserRequest.newEmail(),
        encodedNewPassword);

    Optional<BinaryContent> binaryContent = profile.map(
        file -> {
          try {
            if (user.getProfile() == null) {
              BinaryContent bc = new BinaryContent(file.getOriginalFilename(), file.getSize(),
                  file.getContentType()
              );
              BinaryContent updated = binaryContentRepository.save(bc);
              eventPublisher.publishEvent(new BinaryContentCreatedEvent(updated.getId(), file.getBytes()));
              return updated;
            } else {
              user.getProfile()
                  .update(file.getOriginalFilename(), file.getSize(), file.getContentType());
              eventPublisher.publishEvent(new BinaryContentCreatedEvent(user.getProfile().getId(), file.getBytes()));
              return binaryContentRepository.save(user.getProfile());
            }
          } catch (IOException e) {
            throw new RuntimeException("이미지 가져오는데 실패");
          }
        }
    );
    user.update(binaryContent.orElse(null));

    User updated = userRepository.save(user);

    log.info("유저 정보 업데이트: id={}", updated.getId());
    if (updated.getProfile() != null) {
      log.info("프로필 사진 업로드: {}", updated.getProfile().getId());
    }
    log.debug("이름: {}, 이메일: {}", updated.getUsername(), updated.getEmail());

    return updated;
  }

  @Override
  @CacheEvict(value = "userCache", key = "#userId")
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
    // 유저 id로 삭제
    userRepository.deleteById(userId);
    log.info("유저 삭제: id={}", userId);
  }
}
