package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.Role;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentUploader;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
  private final PasswordEncoder passwordEncoder;
  private final BinaryContentUploader uploader;

  @Override
  public User create(CreateUserRequest request, MultipartFile profile) {

    User userByUserName = userRepository.findByUsername(request.username()).orElse(null);
    User userByEmail = userRepository.findByEmail(request.email()).orElse(null);
    if (userByUserName != null || userByEmail != null) {
      throw new UserAlreadyExistsException();
    }

    BinaryContent binaryContent = uploader.uploadBinaryContent(profile);

    User user = User.builder()
        .username(request.username())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .role(Role.USER)
        .build();

    user.setProfile(binaryContent);

    User saved = userRepository.save(user);

    log.debug("유저 생성: id={}, 이름: {}, 이메일: {}", saved.getId(), saved.getUsername(), saved.getEmail());

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
      MultipartFile profile) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("User not found. userId: {}", userId);
          return new UserNotFoundException(Map.of("유저 고유 아이디", userId));
        });

    BinaryContent binaryContent = uploader.uploadBinaryContent(profile);

    String rawNewPassword = updateUserRequest.newPassword();
    String encodedNewPassword = null;

    if (rawNewPassword != null) {
      if (passwordEncoder.matches(rawNewPassword, user.getPassword())) {
        throw new IllegalArgumentException("기존과 동일한 비밀번호로는 변경할 수 없습니다.");
      }
      encodedNewPassword = passwordEncoder.encode(rawNewPassword);
    }

    user.update(updateUserRequest.newUsername(), updateUserRequest.newEmail(),
        encodedNewPassword, binaryContent);

    User updated = userRepository.save(user);

    log.debug("유저 정보 업데이트: id={}, 이름: {}, 이메일: {}", updated.getId(), updated.getUsername(), updated.getEmail());

    return updated;
  }

  @Override
  @CacheEvict(value = "userCache", key = "#userId")
  public void delete(UUID userId) {
    userRepository.deleteById(userId);
    log.info("유저 삭제: id={}", userId);
  }
}
