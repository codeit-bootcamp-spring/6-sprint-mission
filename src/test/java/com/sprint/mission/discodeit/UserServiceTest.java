package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private final UUID userId = UUID.fromString("36e59793-7bdc-4d07-b66b-df7f7c74d9cb");

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("test")
                .email("ex@ex.com")
                .password("1234")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);
        System.out.println("BeforeEach: User 객체 생성됨");
    }

    @Test
    void 사용자_생성_성공() {

        // given
        UserCreateRequestDto request = new UserCreateRequestDto(
                "ex@ex.com",
                "test",
                "1234"
        );

        // when
        UserResponseDto savedUser = userService.create(request, null);

        // then
        assertThat(savedUser.email()).isEqualTo("ex@ex.com");
        assertThat(savedUser.username()).isEqualTo("test");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 사용자_생성_실패() {

        // given - email 형식 틀림
        UserCreateRequestDto request = new UserCreateRequestDto(
                "exex.com",
                "test2",
                "1234"
        );

        // when
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserCreateRequestDto>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("이메일 형식이 올바르지 않습니다.");
    }

    @Test
    void 사용자_수정_성공() {

        // given
        UserUpdateRequestDto request = new UserUpdateRequestDto(
                "new@new.com",
                "newName",
                "newPassword"
        );

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.update(userId, request, null);

        // then
        assertThat(user.getEmail()).isEqualTo("new@new.com");
        assertThat(user.getUsername()).isEqualTo("newName");
        assertThat(user.getPassword()).isEqualTo("newPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 사용자_수정_실패() {

        // given - 이메일 형식 틀림
        UserUpdateRequestDto request = new UserUpdateRequestDto(
                "newnew.com",
                null,
                null
        );

        // when
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserUpdateRequestDto>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("이메일 형식이 올바르지 않습니다.");
    }

    @Test
    void 사용자_삭제_성공() {

        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.delete(userId);

        // then
        assertThat(userRepository.findById(userId)).isPresent();
    }

    @Test
    void 사용자_삭제_실패() {
        // given - 잘못된 userId 사용
        UUID invalidId = UUID.randomUUID();
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.delete(invalidId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");

        // then - 호출되지 않았음을 검증
        verify(userRepository, never()).delete(any());
    }

}
