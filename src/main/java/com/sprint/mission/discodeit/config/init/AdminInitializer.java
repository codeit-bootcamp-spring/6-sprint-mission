package com.sprint.mission.discodeit.config.init;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 프로젝트 실행 시 users 테이블에 admin 유저가 한명도 없으면 새로 생성함. (미션9)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${discodeit.setup.admin.email}")
    private String defaultAdminEmail;

    @Value("${discodeit.setup.admin.username}")
    private String defaultAdminUsername;

    @Value("${discodeit.setup.admin.password}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) {

        if (!userRepository.existsByRole(Role.ADMIN)) {
            User admin = User.builder()
                    .email(defaultAdminEmail)
                    .username(defaultAdminUsername)
                    .password(passwordEncoder.encode(defaultAdminPassword))
                    .build();

            userRepository.save(admin);
            log.info("Admin account has been created");
        }
    }
}
