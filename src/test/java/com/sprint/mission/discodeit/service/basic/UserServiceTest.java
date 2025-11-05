package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("사용자 생성 테스트")
  void createUserTest() {
    // given
    CreateUserRequest request = CreateUserRequest.builder()
        .username("user")
        .email("user@gmail.com")
        .password("password")
        .build();

    User savedUser = new User("user", "user@gmail.com", "password");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // when
    User response = userService.create(request, Optional.empty());

    // then
    assertThat(response.getUsername()).isEqualTo("user");
    assertThat(response.getEmail()).isEqualTo("user@gmail.com");
    assertThat(response.getPassword()).isEqualTo("password");
    verify(userRepository).save(any(User.class));
  }
}
