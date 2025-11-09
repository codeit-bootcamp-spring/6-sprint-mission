package com.sprint.mission.discodeit.controller;

import static org.assertj.core.api.Assertions.assertThat;

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
class ChannelControllerIntegrationTest {

  @Autowired
  private ApplicationContext context;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @DisplayName("채널 get api 통합 테스트")
  void getChannelByUserId_integrationTest() {
    // given
    UUID userId = UUID.randomUUID();

    String url = "http://localhost:" + port + "/api/channels?userId=" + userId;

    // then
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    // when
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("[]");
    assertThat(context.getBean("channelController")).isInstanceOf(ChannelController.class);
    assertThat(context.getBean(ChannelController.class)).isNotNull();
  }
}
