package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({AppConfig.class})
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private final String testUsername = "tester";
  private final String testEmail = "test@example.com";
  private final String testPassword = "Testpass123@";
  private final String testHashedPassword = "hashedPassword123@";
  private UserEntity testUserEntity;

  @BeforeEach
  void setUp() {

    userRepository.deleteAll();

    testUserEntity = UserEntity.builder()
        .username(testUsername)
        .email(testEmail)
        .password(testHashedPassword)
        .build();

  }

  @Test
  @DisplayName("이메일로 사용자 조회 - 성공")
  void findByEmail_Success() {

    //given
    userRepository.save(testUserEntity);

    //when
    Optional<UserEntity> foundUser = userRepository.findByEmail(testEmail);

    //then
    assertTrue(foundUser.isPresent());
    assertEquals(testEmail, foundUser.get().getEmail());

  }

  @Test
  @DisplayName("이메일로 사용자 조회 - 실패: 존재하지 않는 이메일")
  void findByEmail_Fail_NoSuchUser() {

    //given
    userRepository.save(testUserEntity);

    //when
    Optional<UserEntity> foundUser = userRepository.findByEmail("nonexistent@example.com");

    //then
    assertFalse(foundUser.isPresent());

  }

  @Test
  @DisplayName("사용자명으로 사용자 조회 - 성공")
  void findByUsername_Success() {

    //given
    userRepository.save(testUserEntity);

    //when
    Optional<UserEntity> foundUser = userRepository.findByUsername(testUsername);

    //then
    assertTrue(foundUser.isPresent());
    assertEquals(testUsername, foundUser.get().getUsername());

  }

  @Test
  @DisplayName("사용자명으로 사용자 조회 - 실패: 존재하지 않는 사용자명")
  void findByUsername_Fail_NoSuchUser() {

    //given
    userRepository.save(testUserEntity);

    //when
    Optional<UserEntity> foundUser = userRepository.findByUsername("nonexistentuser");

    //then
    assertTrue(foundUser.isEmpty());

  }

  @Test
  @DisplayName("이메일 또는 사용자명으로 사용자 존재 확인 - 성공")
  void existsByEmailOrUsername_Success() {

    //given
    userRepository.save(testUserEntity);

    //when & then
    assertTrue(userRepository.existsByEmailOrUsername(testEmail, "nonexistent"));
    assertTrue(userRepository.existsByEmailOrUsername("nonexistent@example.com", testUsername));
    assertTrue(userRepository.existsByEmailOrUsername(testEmail, testUsername));

  }

  @Test
  @DisplayName("이메일 또는 사용자명으로 사용자 존재 확인 - 실패: 둘 다 존재하지 않음")
  void existsByEmailOrUsername_Fail_NoSuchUser() {

    //when & then
    assertFalse(userRepository.existsByEmailOrUsername("nonexistent@example.com", "nonexistent"));

  }

  @Test
  @DisplayName("사용자 삭제 - 성공")
  void deleteById_Success() {

    //given
    userRepository.save(testUserEntity);

    //when
    userRepository.deleteById(testUserEntity.getId());

    //then
    assertFalse(userRepository.existsById(testUserEntity.getId()));

  }

  @Test
  @DisplayName("사용자 삭제 - 실패: 존재하지 않는 ID")
  void deleteById_Fail_NoSuchUser() {

    // given
    UUID nonExistentId = UUID.randomUUID();

    // when & then
    assertDoesNotThrow(() -> userRepository.deleteById(nonExistentId));

  }

}