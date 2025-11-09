package com.sprint.mission.discodeit.controller;


import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

// todo - webEnvironment 옵션
// todo - api 통합 테스트
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserController userController;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ApplicationContext context;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @DisplayName("유저 get api 통합 테스트")
  void getUser_integrationTest() {

    // given
    String url = "http://localhost:" + port + "/api/users";

    // when
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("[]");
    assertThat(context.getBean("userController")).isInstanceOf(UserController.class);
    assertThat(context.getBean(UserController.class)).isNotNull();
  }
}
