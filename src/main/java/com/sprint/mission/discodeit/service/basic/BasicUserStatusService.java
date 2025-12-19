package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDTO;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.exception.userstatus.AllReadyExistUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.NoSuchUserStatusException;
import com.sprint.mission.discodeit.mapper.UserStatusEntityMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusEntityMapper userStatusEntityMapper;

  @Transactional
  @Override
  public UserStatusDTO.UserStatus createUserStatus(UserStatusDTO.CreateUserStatusCommand request) {

    if (!userRepository.existsById(request.userId())) {
      throw new NoSuchUserException();
    }

    if (existUserStatusByUserId(request.userId())) {
      throw new AllReadyExistUserStatusException(Map.of("userId", request.userId()));
    }

    UserEntity userEntity = userRepository.findById(request.userId()).get();

    UserStatusEntity userStatusEntity = UserStatusEntity.builder()
        .user(userEntity)
        .build();

    return userStatusEntityMapper.toUserStatus(userStatusRepository.save(userStatusEntity));

  }

  @Override
  public boolean existUserStatusById(UUID id) {
    return userStatusRepository.existsById(id);
  }

  @Override
  public boolean existUserStatusByUserId(UUID userId) {
    return userStatusRepository.existsByUserId(userId);
  }

  @Override
  public UserStatusDTO.UserStatus findUserStatusById(UUID id) {

    UserStatusEntity userStatusEntity = userStatusRepository.findById(id)
        .orElseThrow(NoSuchUserStatusException::new);

    return userStatusEntityMapper.toUserStatus(userStatusEntity);

  }

  @Transactional(readOnly = true)
  @Override
  public UserStatusDTO.UserStatus findUserStatusByUserId(UUID userId) {

    if (!userRepository.existsById(userId)) {
      throw new NoSuchUserException();
    }

    UserStatusEntity userStatusEntity = userStatusRepository.findByUserId(userId)
        .orElseThrow(NoSuchUserStatusException::new);

    return userStatusEntityMapper.toUserStatus(userStatusEntity);

  }

  @Override
  public List<UserStatusDTO.UserStatus> findAllUserStatus() {
    return userStatusRepository.findAll().stream()
        .map(userStatusEntityMapper::toUserStatus)
        .toList();

  }

  @PreAuthorize("hasPermission(#request.id(), 'UserStatus', 'UPDATE')")
  @Transactional
  @Override
  public UserStatusDTO.UserStatus updateUserStatus(UserStatusDTO.UpdateUserStatusCommand request) {

    UserStatusEntity userStatusEntity = userStatusRepository.findById(request.id())
        .orElseThrow(NoSuchUserStatusException::new);

    userStatusEntity.updateLastActiveAt(request.lastActiveAt());

    return userStatusEntityMapper.toUserStatus(userStatusRepository.save(userStatusEntity));

  }

  @Transactional
  @Override
  public void deleteUserStatusById(UUID id) {

    if (!userStatusRepository.existsById(id)) {
      throw new NoSuchUserStatusException();
    }

    userStatusRepository.deleteById(id);

  }

  @Transactional
  @Override
  public void deleteUserStatusByUserId(UUID userId) {

    if (!userStatusRepository.existsById(userId)) {
      throw new NoSuchUserStatusException();
    }

    userStatusRepository.deleteByUserId(userId);

  }

  @Transactional
  @Override
  public void deleteAllUserStatusByIdIn(List<UUID> uuidList) {

    uuidList.forEach(uuid -> {
      if (!userStatusRepository.existsById(uuid)) {
        throw new NoSuchUserStatusException();
      }
    });

    userStatusRepository.deleteAllByIdIn(uuidList);

  }
}
