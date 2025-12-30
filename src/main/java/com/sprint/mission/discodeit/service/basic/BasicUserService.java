package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.UserDTO.UpdateUserRoleCommand;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.user.AllReadyExistUserException;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.exception.user.PasswordMismatchException;
import com.sprint.mission.discodeit.mapper.UserEntityMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utils.SecurityUtil;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserEntityMapper userEntityMapper;
  private final SecurityUtil securityUtil;
  private final JwtRegistry jwtRegistry;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public UserDTO.User createUser(UserDTO.CreateUserCommand request) {

    if (userRepository.existsByEmailOrUsername(request.email(), request.username())) {
      log.warn("User with email {} already exists", request.email());
      throw new AllReadyExistUserException(Map.of("email", request.email()));
    }

    UserEntity userEntity = UserEntity.builder()
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .username(request.username())
        .build();

    if (request.profileImage() != null) {

      BinaryContentEntity binaryContentEntity = BinaryContentEntity.builder()
          .fileName(request.profileImage().fileName())
          .size((long) request.profileImage().data().length)
          .contentType(request.profileImage().contentType())
          .build();

      userEntity.updateProfile(binaryContentEntity);

    }

    UserDTO.User user = userEntityMapper.toUser(userRepository.save(userEntity));

    if (request.profileImage() != null) {
      binaryContentStorage.put(user.getProfileId().getId(), request.profileImage().data());
    }

    log.debug("User with id {} created successfully", user.getId());

    return user;

  }

  @Override
  public boolean existUserById(UUID id) {
    return userRepository.existsById(id);
  }

  @Override
  public boolean existUserByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public boolean existUserByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDTO.User findUserById(UUID id) {

    UserEntity userEntity = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User with id {} not found", id);
          throw new NoSuchUserException();
        });

    UserDTO.User user = userEntityMapper.toUser(userEntity);

    return user;

  }

  @Override
  public UserDTO.User findUserByEmail(String email) {

    UserEntity userEntity = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.warn("User with email {} not found", email);
          throw new NoSuchUserException();
        });

    UserDTO.User user = userEntityMapper.toUser(userEntity);

    return user;

  }

  @Override
  public UserDTO.User findUserByUsername(String username) {

    UserEntity userEntity = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("User with username {} not found", username);
          throw new NoSuchUserException();
        });

    UserDTO.User user = userEntityMapper.toUser(userEntity);

    return user;

  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDTO.User> findAllUsers() {

    return userRepository.findAll().stream()
        .map(userEntityMapper::toUser)
        .toList();
  }

  @Transactional
  @PreAuthorize("hasPermission(#request.id(), 'USER', 'UPDATE')")
  @Override
  public UserDTO.User updateUser(UserDTO.UpdateUserCommand request) {

    UserEntity updatedUserEntity = userRepository.findById(request.id())
        .orElseThrow(() -> {
          log.warn("User with id {} not found", request.id());
          throw new NoSuchUserException();
        });

    if (userRepository.existsByEmailOrUsername(request.email(), request.username()) &&
        !updatedUserEntity.getId().equals(request.id())) {
      log.warn("User with email {} or username {} already exists", request.email(),
          request.username());
      throw new AllReadyExistUserException(
          Map.of("email", request.email(), "username", request.username()));
    }

    if (!securityUtil.hashPassword(request.currentPassword())
        .equals(updatedUserEntity.getPassword())) {
      log.warn("Invalid password for user id {}", request.id());
      throw new PasswordMismatchException();
    }

    updatedUserEntity.update(request.username(), request.email(),
        securityUtil.hashPassword(request.currentPassword()));

    if (request.isProfileImageUpdated()) {

      BinaryContentEntity binaryContentEntity = BinaryContentEntity.builder()
          .fileName(request.profileImage().fileName())
          .size((long) request.profileImage().data().length)
          .contentType(request.profileImage().contentType())
          .build();

      if (updatedUserEntity.getProfileId() != null) {
        binaryContentRepository.deleteById(updatedUserEntity.getProfileId().getId());
      }

      updatedUserEntity.updateProfile(binaryContentEntity);
      binaryContentStorage.put(binaryContentRepository.save(binaryContentEntity).getId(),
          request.profileImage().data());

    }

    log.debug("User with id {} updated successfully", request.id());

    return userEntityMapper.toUser(userRepository.save(updatedUserEntity));

  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDTO.User updateUserRole(UpdateUserRoleCommand request) {

    UserEntity updatedUserEntity = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("User with id {} not found", request.userId());
          throw new NoSuchUserException();
        });

    updatedUserEntity.updateRole(request.newRole());

    if (jwtRegistry.hasActiveJwtInformationByUserId(updatedUserEntity.getId())) {
      jwtRegistry.invalidateJwtInformationByUserId(updatedUserEntity.getId());
    }

    log.debug("User role with id {} updated successfully", request.userId());

    return userEntityMapper.toUser(updatedUserEntity);

  }

  @PreAuthorize("hasPermission(#id, 'USER', 'DELETE')")
  @Override
  public void deleteUserById(UUID id) {

    UserEntity userEntity = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User with id {} not found", id);
          throw new NoSuchUserException();
        });

    binaryContentRepository.deleteById(userEntity.getProfileId().getId());
    userRepository.deleteById(id);

    log.debug("User with id {} deleted successfully", id);

  }
}
