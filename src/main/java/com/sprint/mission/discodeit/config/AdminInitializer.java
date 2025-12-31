package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (!userRepository.existsByRole(Role.ADMIN)) {
            User admin = new User(
                    "admin",
                    "admin@discodeit.com",
                    passwordEncoder.encode("admin1234"),
                    null,
                    Role.ADMIN
            );
            userRepository.save(admin);

            UserStatus adminStatus = new UserStatus(
                    admin,
                    Instant.now()
            );
            userStatusRepository.save(adminStatus);

            System.out.println("어드민 계정과 상태 정보가 정상적으로 초기화되었습니다.");
        }
    }
}