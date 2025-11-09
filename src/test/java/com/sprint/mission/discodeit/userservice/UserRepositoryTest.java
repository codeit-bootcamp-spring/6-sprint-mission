package com.sprint.mission.discodeit.userservice;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.support.UserFixture;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;


    @Test
    @DisplayName("성공 검증")
    public void findAll_success() {
        BinaryContent binaryContent = BinaryContent.builder()
                .fileName("파일")
                .size(10L)
                .contentType("txt")
                .build();

        User user = UserFixture.createUser(binaryContent);

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .build();
        UserFixture.setStatus(user,userStatus);


        binaryContentRepository.save(binaryContent);
        userRepository.save(user);
        userStatusRepository.save(userStatus);


        //when
        List<User> users = userRepository.findAll();

        System.out.println(users);

        //then
        assertThat(users).isNotEmpty();
    }

    //@Test
    //@DisplayName("실패 검증")
    public void findAll_fail() {}
}
