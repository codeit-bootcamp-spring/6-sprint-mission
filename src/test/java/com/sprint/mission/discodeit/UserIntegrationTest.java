package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// TODO binary_content 테이블을 인식하지 못하는 문제 발생
//  -> 테스트 불가능
//  아직 해결 못 한 상태.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EntityScan(basePackages = "com.sprint.mission.discodeit.entity")
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private final UUID userId = UUID.fromString("36e59793-7bdc-4d07-b66b-df7f7c74d9cb");

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = User.builder()
                .username("test")
                .email("ex@ex.com")
                .password("1234")
                .build();
//        ReflectionTestUtils.setField(user, "id", userId);
        System.out.println("BeforeEach: User 객체 생성됨");
    }

    @Test
    @DisplayName("사용자 생성 API - 성공")
    void 사용자_생성_성공() {
        // given
        UserCreateRequestDto dto = new UserCreateRequestDto(
                "test@example.com", "nickname1", "password123"
        );

        // when
        UserResponseDto response = userService.create(dto, null);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo(dto.email());
        assertThat(response.email()).isEqualTo(dto.email());
        assertThat(response.username()).isEqualTo(dto.username());
    }

    @Test
    @DisplayName("사용자 수정 API - 닉네임 변경 성공")
    void 사용자_수정_성공() {
        // given
        User savedUser = userRepository.save(user);
        String newUsername = "newnick";
        UserUpdateRequestDto request = new UserUpdateRequestDto(null, newUsername, null);

        // when
        UserResponseDto updatedUserDto = userService.update(savedUser.getId(), request, null);

        // then
        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updatedUserDto.username()).isEqualTo(newUsername);
        assertThat(foundUser.getUsername()).isEqualTo(newUsername);
    }

    @Test
    @DisplayName("사용자 목록 조회 API - 2명 이상 반환")
    void 사용자_조회_성공() {
        // given
        userRepository.save(user);
        userRepository.save(User.builder()
                .username("test2")
                .email("b@b.com")
                .password("pw")
                .build());

        // when
        List<UserResponseDto> users = userService.findAll();

        // then
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(UserResponseDto::email)
                .containsExactlyInAnyOrder("ex@ex.com", "b@b.com");
    }

    @Test
    @DisplayName("사용자 삭제 API - 성공")
    void 사용자_삭제_성공() {
        // given
        User savedUser = userRepository.save(user);

        // when
        userService.delete(savedUser.getId());

        // then
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }
}
