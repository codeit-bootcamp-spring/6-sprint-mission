package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatus;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(CreateUserStatus createUserStatus) {
    if (userStatusRepository.existsById(createUserStatus.userId())) {
      throw new IllegalArgumentException("이미 유저상태 객체가 있습니다");
    }
    User user = userRepository.findById(createUserStatus.userId())
        .orElseThrow(() -> {
          log.warn("User not found. userId: {}", createUserStatus.userId());
          return new UserNotFoundException(Map.of("유저 고유 아이디", createUserStatus.userId()));
        });
    UserStatus userStatus = new UserStatus(user, Instant.now());
    user.setUserStatus(userStatus);
    return userStatusRepository.save(userStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatus find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.warn("UserStatus not found. userStatusId: {}", userStatusId);
          return new UserStatusNotFoundException();
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public UserStatus update(UUID userId, UpdateUserStatusRequest updateUserStatusRequest) {
    UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
        .orElseThrow(() -> {
          log.warn("UserStatus not found. userId: {}", userId);
          return new UserNotFoundException(Map.of("유저 고유 아이디", userId));
        });
    userStatus.update(updateUserStatusRequest.newLastActiveAt());
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus updateByUserId(UUID userId) {
    UserStatus userStatus = userStatusRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("UserStatus not found. userId: {}", userId);
          return new UserNotFoundException(Map.of("유저 고유 아이디", userId));
        });
    userStatus.update(Instant.now());
    return userStatusRepository.save(userStatus);
  }

  @Override
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      log.warn("UserStatus not found. userStatusId: {}", userStatusId);
      throw new UserStatusNotFoundException();
    }
    userStatusRepository.deleteById(userStatusId);
  }
}
