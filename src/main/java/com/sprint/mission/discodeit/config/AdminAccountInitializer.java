package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${discodeit.admin.username:admin}")
  private String adminUsername;

  @Value("${discodeit.admin.email:admin@example.com}")
  private String adminEmail;

  @Value("${discodeit.admin.password:admin1234}")
  private String adminPassword;

  @Override
  public void run(String... args) {
    if (userRepository.existsByRole(Role.ADMIN)) {
      return;
    }

    if (userRepository.existsByUsername(adminUsername) || userRepository.existsByEmail(adminEmail)) {
      log.warn("관리자 계정을 생성하지 못했습니다. username 또는 email이 이미 사용 중입니다: username={}, email={}",
          adminUsername, adminEmail);
      return;
    }

    String encodedPassword = passwordEncoder.encode(adminPassword);
    User admin = new User(adminUsername, adminEmail, encodedPassword, null, Role.ADMIN);

    userRepository.save(admin);
    log.info("관리자 계정을 초기화했습니다: username={}", adminUsername);
  }
}
