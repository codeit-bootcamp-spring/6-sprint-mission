package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.user.NoSuchUserException;
import com.sprint.mission.discodeit.exception.user.PasswordMismatchException;
import com.sprint.mission.discodeit.mapper.UserEntityMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.SecurityUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicAuthService 테스트")
class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserEntityMapper userEntityMapper;

  @Mock
  private SecurityUtil securityUtil;

  @InjectMocks
  private BasicAuthService basicAuthService;

  private final String testUsername = "testuser";
  private final String testPassword = "testpass123@";
  private final String testHashedPassword = "hashedPassword123@";
  private UserEntity testUserEntity;
  private UserDTO.User expectedUserDto;
  private UserDTO.LoginCommand loginCommand;

  @BeforeEach
  void setUp() {

    testUserEntity = UserEntity.builder()
        .username(testUsername)
        .password(testHashedPassword)
        .build();

    expectedUserDto = UserDTO.User.builder()
        .username(testUsername)
        .password(testHashedPassword)
        .build();

    loginCommand = new UserDTO.LoginCommand(testUsername, testPassword);

  }

  /*@Test
  @DisplayName("로그인 성공 테스트")
  void login_success() {

    //given
    when(userRepository.findByUsername(testUsername))
        .thenReturn(Optional.of(testUserEntity));
    when(securityUtil.hashPassword(testPassword))
        .thenReturn(testHashedPassword);
    when(userEntityMapper.toUser(testUserEntity))
        .thenReturn(expectedUserDto);

    //when
    UserDTO.User result = basicAuthService.login(loginCommand);

    //then
    assertNotNull(result);
    assertEquals(expectedUserDto, result);

  }

  @Test
  @DisplayName("로그인 실패 테스트 - 사용자 없음")
  void login_fail_no_such_user() {

    //given
    when(userRepository.findByUsername(testUsername))
        .thenReturn(Optional.empty());

    //when & then
    assertThrows(NoSuchUserException.class, () -> {
      basicAuthService.login(loginCommand);
    });

  }

  @Test
  @DisplayName("로그인 실패 테스트 - 비밀번호 일치하지 않음")
  void login_Fail_passwordMismatch() {

    //given
    when(userRepository.findByUsername(testUsername))
        .thenReturn(Optional.of(testUserEntity));
    when(securityUtil.hashPassword(testPassword))
        .thenReturn("wrongHashedPassword");

    //when & then
    assertThrows(PasswordMismatchException.class, () -> {
      basicAuthService.login(loginCommand);
    });

  }*/

}