package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage storage;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("프로필 없는 사용자 생성 테스트")
  void createUser_noProfileImage() {
    // given
    CreateUserRequest request = CreateUserRequest.builder()
        .username("user")
        .email("user@gmail.com")
        .password("password")
        .build();

    User savedUser = new User("user", "user@gmail.com", "password");
    given(userRepository.save(any(User.class))).willReturn(savedUser);

    // when
    User createdUser = userService.create(request, Optional.empty());

    // then
    assertThat(createdUser.getUsername()).isEqualTo("user");
    assertThat(createdUser.getEmail()).isEqualTo("user@gmail.com");
    assertThat(createdUser.getPassword()).isEqualTo("password");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("프로필 있는 사용자 생성 테스트")
  void createUser_withProfileImage() {
    // given
    CreateUserRequest request = CreateUserRequest.builder()
        .username("user")
        .email("user@gmail.com")
        .password("password")
        .build();

    MultipartFile profile = new MockMultipartFile(
        "profile",
        "profile.png",
        "image/png",
        new byte[]{1, 2, 3, 4}
    );

    // create 안에서 save 호출 시, 인자로 받은 객체를 그대로 반환하도록 설정
    given(userRepository.save(any(User.class)))
        .willAnswer(invocation -> invocation.getArgument(0));
    given(binaryContentRepository.save(any(BinaryContent.class)))
        .willAnswer(invocation -> invocation.getArgument(0));
    // 영속화는 안되기에 UUID 타입의 id는 nullable로 처리
    given(storage.put(nullable(UUID.class), any(byte[].class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    User createdUser = userService.create(request, Optional.of(profile));
    BinaryContent createdProfile = createdUser.getProfile();

    // then
    assertThat(createdUser.getUsername()).isEqualTo("user");
    assertThat(createdUser.getEmail()).isEqualTo("user@gmail.com");
    assertThat(createdUser.getPassword()).isEqualTo("password");
    verify(userRepository).save(any(User.class));

    assertThat(createdProfile).isNotNull();
    assertThat(createdProfile.getFileName()).isEqualTo("profile.png");
    assertThat(createdProfile.getContentType()).isEqualTo("image/png");
    assertThat(createdProfile.getSize()).isEqualTo(new byte[]{1, 2, 3, 4}.length);
    verify(binaryContentRepository).save(any(BinaryContent.class));
    verify(storage).put(nullable(UUID.class), any(byte[].class));
  }

  @Test
  @DisplayName("중복된 사용자 이름으로 인한 사용자 생성 실패 테스트")
  void createUser_duplicateUsername_throwsException() {
    // given
    CreateUserRequest request = CreateUserRequest.builder()
        .username("user")
        .email("user@gmail.com")
        .password("password")
        .build();

    User userByUsername = new User("user", "user@gmail.com", "password");
    given(userRepository.findByUsername("user")).willReturn(Optional.of(userByUsername));

    // when & then
    assertThrows(IllegalArgumentException.class, () -> {
      userService.create(request, Optional.empty());
    });
  }

  @Test
  @DisplayName("사용자 정보 업데이트 테스트")
  void updateUserTest() {
    // given
    UpdateUserRequest request = UpdateUserRequest.builder()
        .newUsername("newUser")
        .newEmail("newUser@gmail.com")
        .newPassword("newPassword")
        .build();

    User user = new User("user", "user@gmail.com", "password");

    /*
    // 스프링 프레임워크가 제공하는 라이브러리와 같음 - ReflectionTestUtils
    try {
      Field idField = BaseEntity.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(user, UUID.randomUUID());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    */

    // reflection을 통해 private id 필드 세팅
    ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

    given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
    given(userStatusRepository.findByUser_Id(any(UUID.class)))
        .willReturn(Optional.of(mock(UserStatus.class)));
    given(userRepository.save(any(User.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    User updatedUser = userService.update(user.getId(), request, Optional.empty());

    // then
    assertThat(updatedUser.getUsername()).isEqualTo("newUser");
    assertThat(updatedUser.getEmail()).isEqualTo("newUser@gmail.com");
    assertThat(updatedUser.getPassword()).isEqualTo("newPassword");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 업데이트 테스트")
  void updateUser_nonExistentUser_throwsException() {
    // given
    UpdateUserRequest request = UpdateUserRequest.builder()
        .newUsername("newUser")
        .newEmail("newUser@gmail.com")
        .newPassword("newPassword")
        .build();

    UUID requestId = UUID.randomUUID();

    given(userRepository.findById(requestId)).willReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> {
      userService.update(requestId, request, Optional.empty());
    });
  }
}
