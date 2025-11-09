package com.sprint.mission.discodeit.channelservice;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql("/test-user-data.sql")
public class ChannelApiTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("공개 채널 생성 검증")
    void createPublicApiTest_success() {
        PublicChannelCreateRequest request = PublicChannelCreateRequest.builder()
                .name("테스트")
                .description("테스트중")
                .build();
        ResponseEntity<ChannelDto> response = this.restTemplate.postForEntity("/api/channels/public", request, ChannelDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ChannelDto channelDto = response.getBody();
        assertThat(channelDto).isNotNull();

    }

    @Test
    @DisplayName("비공개 채널 생성 검증")
    void createCPrivateApiTest_success() {
        List<UUID> userIds = new ArrayList<>(List.of(UUID.fromString("33ac0646-e2a0-4f51-ac8a-1d945f0b84a1")
                , UUID.fromString("0d757dfb-c7d7-4616-bc16-7e33a588186d")));

        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(userIds)
                .build();

        ResponseEntity<ChannelDto> response = this.restTemplate.postForEntity("/api/channels/private", request, ChannelDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ChannelDto channelDto = response.getBody();
        assertThat(channelDto).isNotNull();

    }

    @Test
    @DisplayName("공개 채널 잘못된 요청 실패 검증")
    void createPublicApiTest_fail() {
        PublicChannelCreateRequest request = PublicChannelCreateRequest.builder()
                .name(null)
                .description("테스트중")
                .build();
        ResponseEntity<ChannelDto> response = this.restTemplate.postForEntity("/api/channels/public", request, ChannelDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("비공개 채널 잘못된 요청 실패 검증")
    void createCPrivateApiTest_fail() {
        List<UUID> userIds = new ArrayList<>();

        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(userIds)
                .build();

        ResponseEntity<ChannelDto> response = this.restTemplate.postForEntity("/api/channels/private", request, ChannelDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


}
