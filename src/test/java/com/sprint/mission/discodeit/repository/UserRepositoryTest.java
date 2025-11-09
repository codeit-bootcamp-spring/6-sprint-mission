package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager em;

  @BeforeEach
  void setUp() {
    // given
    User user = new User("test", "email@gmail.com", "password");
    userRepository.save(user);

    // db에 바로 반영, 영속성 컨텍스트 초기화
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("이메일로 사용자 조회 테스트")
  void findByEmail() {
    // when
    User foundUser = userRepository.findByEmail("email@gmail.com").get();

    // then
    assertThat(foundUser.getUsername()).isEqualTo("test");
    assertThat(foundUser.getEmail()).isEqualTo("email@gmail.com");
    assertThat(foundUser.getPassword()).isEqualTo("password");
  }

  @Test
  @DisplayName("사용자 이름으로 사용자 조회 테스트")
  void findByUsername() {
    // when
    User foundUser = userRepository.findByUsername("test").get();

    // then
    assertThat(foundUser.getUsername()).isEqualTo("test");
    assertThat(foundUser.getEmail()).isEqualTo("email@gmail.com");
    assertThat(foundUser.getPassword()).isEqualTo("password");
  }
}
