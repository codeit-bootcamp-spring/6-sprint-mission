package com.sprint.mission.discodeit.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.UUID;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
class MessageControllerIntegrationTest {

  @Autowired
  private ApplicationContext context;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @DisplayName("메시지 get api 통합 테스트")
  void getMessage_integrationTest() {
    // given
    UUID messageId = UUID.randomUUID();

    String url = "http://localhost:" + port + "/api/messages?channelId=" + messageId;

    // when
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    // then
    String content = JsonPath.read(response.getBody(), "$.content").toString();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(content).isEqualTo("[]");
    assertThat(context.getBean("messageController")).isInstanceOf(MessageController.class);
    assertThat(context.getBean(MessageController.class)).isNotNull();
  }
}
