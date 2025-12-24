package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.exception.user.AllReadyExistUserException;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.exception.user.PasswordMismatchException;
import com.sprint.mission.discodeit.mapper.UserEntityMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utils.SecurityUtil;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicUserService 테스트")
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private UserEntityMapper userEntityMapper;
  @Mock
  private SecurityUtil securityUtil;

  @InjectMocks
  private BasicUserService basicUserService;

  private final UUID testUserId = UUID.randomUUID();
  private final String testUsername = "tester";
  private final String testEmail = "test@example.com";
  private final String testPassword = "Testpass123@";
  private final String testHashedPassword = "hashedPassword123@";
  private UserEntity testUserEntity;
  private UserStatusEntity testUserStatusEntity;
  private UserDTO.User testUserDto;

  @BeforeEach
  void setUp() {

    testUserEntity = UserEntity.builder()
        .username(testUsername)
        .email(testEmail)
        .password(testHashedPassword)
        .build();

    testUserStatusEntity = UserStatusEntity.builder()
        .user(testUserEntity)
        .lastActiveAt(java.time.Instant.now())
        .build();

    testUserDto = UserDTO.User.builder()
        .id(testUserId)
        .username(testUsername)
        .email(testEmail)
        .password(testHashedPassword)
        .isOnline(true)
        .build();

    testUserEntity.updateUserStatus(testUserStatusEntity);

  }

  @Test
  @DisplayName("사용자 생성 성공 테스트")
  void createUser_Success() {

    // given
    UserDTO.CreateUserCommand command = new UserDTO.CreateUserCommand(
        testUsername,
        testEmail,
        testPassword,
        "This is a test user",
        null
    );

    when(userRepository.existsByEmailOrUsername(testEmail, testUsername))
        .thenReturn(false);
    when(userRepository.save(any(UserEntity.class)))
        .thenReturn(testUserEntity);
    when(userEntityMapper.toUser(testUserEntity))
        .thenReturn(testUserDto);

    // when
    UserDTO.User result = basicUserService.createUser(command);

    // then
    assertNotNull(result);
    assertEquals(testUsername, result.getUsername());
    assertEquals(testEmail, result.getEmail());

  }

  @Test
  @DisplayName("사용자 생성 실패 테스트 - 이미 존재하는 이메일")
  void createUser_Fail_emailAlreadyExists() {

    // given
    UserDTO.CreateUserCommand command = new UserDTO.CreateUserCommand(
        testUsername,
        testEmail,
        testPassword,
        "This is a test user",
        null
    );

    when(userRepository.existsByEmailOrUsername(testEmail, testUsername)).thenReturn(true);

    // when & then
    assertThrows(AllReadyExistUserException.class, () -> {
      basicUserService.createUser(command);
    });

  }

  @Test
  @DisplayName("사용자 정보 업데이트 성공 테스트")
  void updateUser_Success() {

    // given
    String updatedUsername = "updateduser";
    String updatedEmail = "updated@example.com";
    String newPassword = "newPass123@";
    String newHashedPassword = "newHashedPass123@";

    UserDTO.UpdateUserCommand command = new UserDTO.UpdateUserCommand(
        testUserId,
        updatedUsername,
        updatedEmail,
        testPassword,
        newPassword,
        "d",
        false,
        null
    );

    when(userRepository.findById(testUserId))
        .thenReturn(Optional.of(testUserEntity));
    when(securityUtil.hashPassword(testPassword))
        .thenReturn(testHashedPassword);
    when(userRepository.existsByEmailOrUsername(updatedEmail, updatedUsername))
        .thenReturn(false);
    when(userRepository.save(any(UserEntity.class)))
        .thenReturn(testUserEntity);
    when(userEntityMapper.toUser(testUserEntity))
        .thenReturn(testUserDto);

    // when
    UserDTO.User result = basicUserService.updateUser(command);

    // then
    assertNotNull(result);

  }

  @Test
  @DisplayName("사용자 정보 업데이트 실패 테스트 - 잘못된 비밀번호")
  void updateUser_Fail_invalidPassword() {
    // given
    UserDTO.UpdateUserCommand command = new UserDTO.UpdateUserCommand(
        testUserId,
        "updateduser",
        "updated@example.com",
        "wrongpassword",
        "newPass123@",
        "d",
        false,
        null
    );

    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));

    // when & then
    assertThrows(PasswordMismatchException.class, () -> {
      basicUserService.updateUser(command);
    });

  }

  @Test
  @DisplayName("사용자 ROLE 업데이트 성공 테스트")
  void updateUserRole_Success() {

    // given
    UserDTO.UpdateUserRoleCommand command = new UserDTO.UpdateUserRoleCommand(
        testUserId,
        Role.ADMIN
    );

    when(userRepository.findById(testUserId))
        .thenReturn(Optional.of(testUserEntity));
    when(userEntityMapper.toUser(testUserEntity))
        .thenReturn(testUserDto);

    // when
    UserDTO.User result = basicUserService.updateUserRole(command);

    // then
    assertNotNull(result);

  }

  @Test
  @DisplayName("사용자 삭제 성공 테스트")
  void deleteUserById_Success() {

    // given
    BinaryContentEntity profileImage = BinaryContentEntity.builder()
        .build();
    testUserEntity.updateProfile(profileImage);

    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));

    // when & then
    assertDoesNotThrow(() -> {
      basicUserService.deleteUserById(testUserId);
    });

  }

  @Test
  @DisplayName("사용자 삭제 실패 테스트 - 존재하지 않는 사용자")
  void deleteUserById_Fail_userNotFound() {

    // given
    when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(NoSuchUserException.class, () -> {
      basicUserService.deleteUserById(testUserId);
    });

  }

  @Test
  @DisplayName("사용자 ID로 조회 성공 테스트")
  void findUserById_Success() {

    // given
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
    when(userStatusRepository.findByUserId(testUserId)).thenReturn(Optional.of(testUserStatusEntity));
    when(userEntityMapper.toUser(testUserEntity)).thenReturn(testUserDto);

    // when
    UserDTO.User result = basicUserService.findUserById(testUserId);

    // then
    assertNotNull(result);
    assertEquals(testUserId, result.getId());
    assertEquals(testUsername, result.getUsername());
  }

  @Test
  @DisplayName("사용자 ID로 조회 실패 테스트 - 존재하지 않는 사용자")
  void findUserById_Fail_userNotFound() {

    // given
    when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(NoSuchUserException.class, () -> {
      basicUserService.findUserById(testUserId);
    });

  }

}