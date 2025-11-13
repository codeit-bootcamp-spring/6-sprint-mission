package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.enums.BinaryContentType;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    // 엔터티를 못 찾는 에러 발생
//    @Autowired
//    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {

        BinaryContent profileImage1 = BinaryContent.builder()
                .fileName("profile1.png")
                .extension("image/png")
                .type(BinaryContentType.PROFILE_IMAGE)
                .size(5L)
                .build();

        user1 = User.builder()
                .username("alice")
                .email("alice@example.com")
                .password("password")
                .createdAt(Instant.now())
                .profileImage(profileImage1)
                .build();

        user2 = User.builder()
                .username("bob")
                .email("bob@example.com")
                .password("password2")
                .createdAt(Instant.now())
                .build();

        UserStatus status1 = UserStatus.builder()
                .user(user1)
                .lastActiveAt(Instant.now())
                .build();

        UserStatus status2 = UserStatus.builder()
                .user(user2)
                .lastActiveAt(Instant.now().minusSeconds(600))
                .build();

        // 양방향 연관관계 설정
        user1.setUserStatus(status1);
        user2.setUserStatus(status2);
        profileImage1.setUser(user1);

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    @DisplayName("findByUsernameWithStatusAndProfile() 성공 - UserStatus와 Profile 모두 조회")
    void testFindByUsernameWithStatusAndProfile_성공() {

        // given - setUp()

        // when
        Optional<User> found = userRepository.findByUsernameWithStatusAndProfile("alice");

        // then
        assertThat(found).isPresent();

        User user = found.get();
        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getUserStatus()).isNotNull();
        assertThat(user.getProfileImage()).isNotNull();
        assertThat(user.getUserStatus().isOnline()).isTrue();
    }

    @Test
    @DisplayName("findByUsernameWithStatusAndProfile() 실패 - 존재하지 않는 username")
    void testFindByUsernameWithStatusAndProfile_실패() {
        Optional<User> found = userRepository.findByUsernameWithStatusAndProfile("charlie");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAllWithStatusAndProfile() 조회 - 페이징/정렬")
    void testFindAllWithStatusAndProfile() {

        // given - setUp()

        // when
        List<User> users = userRepository.findAllWithStatusAndProfile();

        // then
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("alice", "bob");
    }
}
