package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // ADMIN 권한을 가진 사용자가 하나도 없을 때만 초기화
        if (!userRepository.existsByRole(Role.ADMIN)) {
            User admin = new User(
                    "admin",
                    "admin@discodeit.com",
                    passwordEncoder.encode("admin1234"), // 초기 비밀번호
                    null,
                    Role.ADMIN
            );
            userRepository.save(admin);
        }
    }
}